#!/usr/bin/env bash
set -euo pipefail

STATUS="${1:?usage: notify-discord.sh <status> <message>}"
MESSAGE="${2:?usage: notify-discord.sh <status> <message>}"
AWS_REGION="${AWS_REGION:-ap-northeast-2}"
AWS_DEFAULT_REGION="${AWS_DEFAULT_REGION:-$AWS_REGION}"
PROJECT="${PROJECT:-devsolve}"
ENVIRONMENT="${ENVIRONMENT:-dev}"
PARAMETER_PREFIX="${PARAMETER_PREFIX:-/${PROJECT}/${ENVIRONMENT}}"

export AWS_REGION AWS_DEFAULT_REGION PROJECT ENVIRONMENT

WEBHOOK_URL="$(aws ssm get-parameter \
  --name "${PARAMETER_PREFIX}/discord/webhook-url" \
  --with-decryption \
  --region "$AWS_REGION" \
  --query Parameter.Value \
  --output text)"

curl -sS -X POST "$WEBHOOK_URL" \
  -H "Content-Type: application/json" \
  -d "{\"content\":\"[${STATUS}] ${MESSAGE}\"}"
