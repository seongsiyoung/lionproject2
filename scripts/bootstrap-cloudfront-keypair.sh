#!/usr/bin/env bash
set -euo pipefail
set +x

AWS_REGION="${AWS_REGION:-ap-northeast-2}"
GITHUB_ENVIRONMENT="${GITHUB_ENVIRONMENT:-dev}"
PROJECT="${PROJECT:-devsolve}"
ENVIRONMENT="${ENVIRONMENT:-dev}"
PARAMETER_NAME=""
FORCE=false
KEEP_FILES=false

usage() {
  cat <<'USAGE'
Usage: scripts/bootstrap-cloudfront-keypair.sh [options]

Generate a CloudFront signed URL key pair, register the public key in GitHub
Secrets, and store the private key in SSM Parameter Store SecureString.

Options:
  --aws-region <region>       AWS region for SSM Parameter Store. Default: ap-northeast-2
  --github-env <environment>  GitHub Environment for CLOUDFRONT_PUBLIC_KEY_PEM. Default: dev
  --project <name>            Project path segment for the default SSM parameter. Default: devsolve
  --environment <name>        Environment path segment for the default SSM parameter. Default: dev
  --parameter-name <name>     Full SSM parameter name. Default: /<project>/<environment>/lesson-file/cloudfront-private-key
  --force                     Overwrite existing GitHub secret and SSM parameter without prompting
  --keep-files                Keep generated key files and print their directory path
  -h, --help                  Show this help

Required tools: openssl, gh, aws
USAGE
}

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

confirm() {
  local answer

  if [[ "$FORCE" == "true" ]]; then
    return 0
  fi

  printf '%s [y/N] ' "$1"
  read -r answer
  [[ "$answer" == "y" || "$answer" == "Y" ]]
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --aws-region)
      AWS_REGION="${2:?--aws-region requires a value}"
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
    --parameter-name)
      PARAMETER_NAME="${2:?--parameter-name requires a value}"
      shift 2
      ;;
    --force)
      FORCE=true
      shift
      ;;
    --keep-files)
      KEEP_FILES=true
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

PARAMETER_NAME="${PARAMETER_NAME:-/${PROJECT}/${ENVIRONMENT}/lesson-file/cloudfront-private-key}"

require_command openssl
require_command gh
require_command aws

echo "GitHub repository:"
gh repo view --json nameWithOwner --jq .nameWithOwner

echo "AWS caller identity:"
aws sts get-caller-identity \
  --query 'join(` `, [`Account=`, Account, `Arn=`, Arn])' \
  --output text

echo "Target GitHub Environment: ${GITHUB_ENVIRONMENT}"
echo "Target SSM parameter: ${PARAMETER_NAME}"
echo "Target AWS region: ${AWS_REGION}"

if ! confirm "Continue and overwrite the CloudFront public key secret/private key parameter if they already exist?"; then
  echo "Canceled."
  exit 1
fi

if aws ssm get-parameter \
  --name "$PARAMETER_NAME" \
  --with-decryption \
  --region "$AWS_REGION" \
  --query Name \
  --output text >/dev/null 2>&1; then
  if [[ "$FORCE" != "true" ]]; then
    echo "SSM parameter already exists: ${PARAMETER_NAME}"
    confirm "Overwrite this SSM SecureString?" || {
      echo "Canceled."
      exit 1
    }
  fi
fi

WORK_DIR="$(mktemp -d)"
PRIVATE_KEY_FILE="${WORK_DIR}/cloudfront-private-key.pem"
PUBLIC_KEY_FILE="${WORK_DIR}/cloudfront-public-key.pem"
DERIVED_PUBLIC_KEY_FILE="${WORK_DIR}/cloudfront-public-key-derived.pem"

cleanup() {
  if [[ "$KEEP_FILES" == "true" ]]; then
    echo "Generated key files kept in: ${WORK_DIR}"
  else
    rm -rf "$WORK_DIR"
  fi
}
trap cleanup EXIT

umask 077
openssl genrsa -out "$PRIVATE_KEY_FILE" 2048 >/dev/null 2>&1
openssl rsa -in "$PRIVATE_KEY_FILE" -pubout -out "$PUBLIC_KEY_FILE" >/dev/null 2>&1
openssl rsa -in "$PRIVATE_KEY_FILE" -pubout -out "$DERIVED_PUBLIC_KEY_FILE" >/dev/null 2>&1

if ! cmp -s "$PUBLIC_KEY_FILE" "$DERIVED_PUBLIC_KEY_FILE"; then
  echo "Generated public key does not match the private key." >&2
  exit 1
fi

PUBLIC_KEY_FINGERPRINT="$(
  openssl rsa -pubin -in "$PUBLIC_KEY_FILE" -outform DER 2>/dev/null \
    | shasum -a 256 \
    | awk '{print $1}'
)"

gh secret set CLOUDFRONT_PUBLIC_KEY_PEM \
  --env "$GITHUB_ENVIRONMENT" \
  < "$PUBLIC_KEY_FILE"

aws ssm put-parameter \
  --name "$PARAMETER_NAME" \
  --type SecureString \
  --value "file://${PRIVATE_KEY_FILE}" \
  --overwrite \
  --region "$AWS_REGION" >/dev/null

echo "CloudFront key pair registered."
echo "GitHub Secret: CLOUDFRONT_PUBLIC_KEY_PEM (${GITHUB_ENVIRONMENT})"
echo "SSM SecureString: ${PARAMETER_NAME}"
echo "Public key SHA-256 fingerprint: ${PUBLIC_KEY_FINGERPRINT}"
