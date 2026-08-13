#!/usr/bin/env bash
set -euo pipefail

STATUS="${1:?usage: notify-discord.sh <status> <message>}"
MESSAGE="${2:?usage: notify-discord.sh <status> <message>}"
WEBHOOK_URL="$(aws ssm get-parameter \
  --name /devsolve/dev/discord/webhook-url \
  --with-decryption \
  --query Parameter.Value \
  --output text)"

curl -sS -X POST "$WEBHOOK_URL" \
  -H "Content-Type: application/json" \
  -d "{\"content\":\"[${STATUS}] ${MESSAGE}\"}"
