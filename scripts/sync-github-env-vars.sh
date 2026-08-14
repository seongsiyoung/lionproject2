#!/usr/bin/env bash
set -euo pipefail

AWS_REGION="${AWS_REGION:-ap-northeast-2}"
GITHUB_ENVIRONMENT="${GITHUB_ENVIRONMENT:-dev}"
PROJECT="${PROJECT:-devsolve}"
ENVIRONMENT="${ENVIRONMENT:-dev}"
DEPLOY_TIMEOUT_SECONDS="${DEPLOY_TIMEOUT_SECONDS:-900}"
BUDGET_EMAIL="${BUDGET_EMAIL:-}"
STAGE="all"
DRY_RUN=false

BOOTSTRAP_DIR="infra/bootstrap"
DEV_DIR="infra/envs/dev"

usage() {
  cat <<'USAGE'
Usage: scripts/sync-github-env-vars.sh [options]

Read Terraform outputs and register the values as GitHub Environment Variables.
This script does not run terraform apply.

Options:
  --stage <stage>             bootstrap, dev, or all. Default: all
  --github-env <environment>  GitHub Environment name. Default: dev
  --project <project>         Value to register as PROJECT. Default: devsolve
  --environment <environment> Value to register as ENVIRONMENT. Default: dev
  --aws-region <region>       Value to register as AWS_REGION. Default: ap-northeast-2
  --budget-email <email>      Value to register as BUDGET_EMAIL
  --deploy-timeout <seconds>  Value to register as DEPLOY_TIMEOUT_SECONDS. Default: 900
  --dry-run                   Print values that would be written without calling gh
  -h, --help                  Show this help

Typical sequence:
  1. terraform -chdir=infra/bootstrap apply
  2. scripts/sync-github-env-vars.sh --stage bootstrap --budget-email you@example.com
  3. Run infra.yml action=apply
  4. scripts/sync-github-env-vars.sh --stage dev

Required tools: terraform, gh
USAGE
}

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

terraform_output_raw() {
  local dir="$1"
  local name="$2"

  terraform -chdir="$dir" output -raw "$name"
}

set_github_variable() {
  local name="$1"
  local value="$2"

  if [[ -z "$value" ]]; then
    echo "Refusing to set empty GitHub variable: ${name}" >&2
    exit 1
  fi

  if [[ "$DRY_RUN" == "true" ]]; then
    echo "[dry-run] ${name}=${value}"
    return 0
  fi

  gh variable set "$name" \
    --env "$GITHUB_ENVIRONMENT" \
    --body "$value"
}

check_dev_backend_bucket() {
  local expected_bucket="$1"
  local configured_bucket

  configured_bucket="$(awk -F\" '/bucket[[:space:]]*=/{print $2; exit}' "$DEV_DIR/backend.tf")"

  if [[ -z "$configured_bucket" ]]; then
    echo "Could not read backend bucket from ${DEV_DIR}/backend.tf" >&2
    exit 1
  fi

  if [[ "$configured_bucket" != "$expected_bucket" ]]; then
    echo "Terraform backend bucket mismatch." >&2
    echo "  bootstrap output: ${expected_bucket}" >&2
    echo "  ${DEV_DIR}/backend.tf: ${configured_bucket}" >&2
    echo "Update backend.tf before running the dev stack." >&2
    exit 1
  fi
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --stage)
      STAGE="${2:?--stage requires a value}"
      shift 2
      ;;
    --github-env)
      GITHUB_ENVIRONMENT="${2:?--github-env requires a value}"
      shift 2
      ;;
    --project)
      PROJECT="${2:?--project requires a value}"
      shift 2
      ;;
    --environment)
      ENVIRONMENT="${2:?--environment requires a value}"
      shift 2
      ;;
    --aws-region)
      AWS_REGION="${2:?--aws-region requires a value}"
      shift 2
      ;;
    --budget-email)
      BUDGET_EMAIL="${2:?--budget-email requires a value}"
      shift 2
      ;;
    --deploy-timeout)
      DEPLOY_TIMEOUT_SECONDS="${2:?--deploy-timeout requires a value}"
      shift 2
      ;;
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

case "$STAGE" in
  bootstrap|dev|all) ;;
  *)
    echo "--stage must be one of: bootstrap, dev, all" >&2
    exit 1
    ;;
esac

if ! [[ "$DEPLOY_TIMEOUT_SECONDS" =~ ^[1-9][0-9]*$ ]]; then
  echo "--deploy-timeout must be a positive integer" >&2
  exit 1
fi

require_command terraform
require_command gh

if [[ "$DRY_RUN" != "true" ]]; then
  echo "GitHub repository:"
  gh repo view --json nameWithOwner --jq .nameWithOwner
fi

echo "Target GitHub Environment: ${GITHUB_ENVIRONMENT}"

set_github_variable AWS_REGION "$AWS_REGION"
set_github_variable PROJECT "$PROJECT"
set_github_variable ENVIRONMENT "$ENVIRONMENT"
set_github_variable DEPLOY_TIMEOUT_SECONDS "$DEPLOY_TIMEOUT_SECONDS"

if [[ "$STAGE" == "bootstrap" || "$STAGE" == "all" ]]; then
  if [[ -z "$BUDGET_EMAIL" ]]; then
    echo "BUDGET_EMAIL is required for bootstrap/all stage. Pass --budget-email or export BUDGET_EMAIL." >&2
    exit 1
  fi

  STATE_BUCKET="$(terraform_output_raw "$BOOTSTRAP_DIR" state_bucket)"
  check_dev_backend_bucket "$STATE_BUCKET"

  set_github_variable BUDGET_EMAIL "$BUDGET_EMAIL"
  set_github_variable AWS_INFRA_ROLE_ARN "$(terraform_output_raw "$BOOTSTRAP_DIR" github_infra_role_arn)"
  set_github_variable ROUTE53_ZONE_ID "$(terraform_output_raw "$BOOTSTRAP_DIR" route53_zone_id)"
fi

if [[ "$STAGE" == "dev" || "$STAGE" == "all" ]]; then
  set_github_variable AWS_DEPLOY_ROLE_ARN "$(terraform_output_raw "$DEV_DIR" github_deploy_role_arn)"
  set_github_variable ECR_REPOSITORY_URL "$(terraform_output_raw "$DEV_DIR" ecr_repository_url)"
  set_github_variable BACKEND_INSTANCE_ID "$(terraform_output_raw "$DEV_DIR" backend_instance_id)"
fi

echo "GitHub Environment Variables synced for stage: ${STAGE}"
