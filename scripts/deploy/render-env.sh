#!/usr/bin/env bash
set -euo pipefail

ENV_FILE="/opt/devsolve/env/app.env"
PRIVATE_KEY_FILE="/opt/devsolve/env/cloudfront-private-key.pem"
APP_UID="${APP_UID:-10001}"
APP_GID="${APP_GID:-10001}"
AWS_REGION="${AWS_REGION:-ap-northeast-2}"
AWS_DEFAULT_REGION="${AWS_DEFAULT_REGION:-$AWS_REGION}"
PROJECT="${PROJECT:-devsolve}"
ENVIRONMENT="${ENVIRONMENT:-dev}"
PARAMETER_PREFIX="${PARAMETER_PREFIX:-/${PROJECT}/${ENVIRONMENT}}"

export AWS_REGION AWS_DEFAULT_REGION PROJECT ENVIRONMENT

mkdir -p "$(dirname "$ENV_FILE")"

get_param() {
  aws ssm get-parameter \
    --name "$1" \
    --region "$AWS_REGION" \
    --query Parameter.Value \
    --output text
}

get_secret() {
  aws ssm get-parameter \
    --name "$1" \
    --with-decryption \
    --region "$AWS_REGION" \
    --query Parameter.Value \
    --output text
}

require_value() {
  local name="$1"
  local value="$2"

  if [[ -z "$value" || "$value" == "None" ]]; then
    echo "Missing required runtime value: ${name}" >&2
    exit 1
  fi
}

get_rds_password() {
  local secret_arn

  secret_arn="$(get_param "${PARAMETER_PREFIX}/db/master-secret-arn")"
  aws secretsmanager get-secret-value \
    --secret-id "$secret_arn" \
    --region "$AWS_REGION" \
    --query SecretString \
    --output text \
    | jq -r '.password'
}

DB_URL="$(get_param "${PARAMETER_PREFIX}/db/url")"
DB_USERNAME="$(get_param "${PARAMETER_PREFIX}/db/username")"
DB_PASSWORD="$(get_rds_password)"
REDIS_HOST="$(get_param "${PARAMETER_PREFIX}/redis/host")"
REDIS_PORT="$(get_param "${PARAMETER_PREFIX}/redis/port")"
REDIS_PASSWORD="$(get_secret "${PARAMETER_PREFIX}/redis/password")"
REDISSON_ADDRESS="rediss://${REDIS_HOST}:${REDIS_PORT}"
REDISSON_PASSWORD="$REDIS_PASSWORD"
JWT_SECRET="$(get_secret "${PARAMETER_PREFIX}/jwt/secret")"
CORS_ALLOWED_ORIGINS="$(get_param "${PARAMETER_PREFIX}/cors/allowed-origins")"
PORTONE_STORE_ID="$(get_param "${PARAMETER_PREFIX}/payment/portone-store-id")"
PORTONE_CHANNEL_KEY="$(get_secret "${PARAMETER_PREFIX}/payment/portone-channel-key")"
PORTONE_API_SECRET="$(get_secret "${PARAMETER_PREFIX}/payment/portone-api-secret")"
INICIS_MID="$(get_param "${PARAMETER_PREFIX}/payment/inicis-mid")"
INICIS_SIGNKEY="$(get_secret "${PARAMETER_PREFIX}/payment/inicis-signkey")"
INICIS_API_KEY="$(get_secret "${PARAMETER_PREFIX}/payment/inicis-api-key")"
INICIS_API_IV="$(get_secret "${PARAMETER_PREFIX}/payment/inicis-api-iv")"
DISCORD_WEBHOOK_URL="$(get_secret "${PARAMETER_PREFIX}/discord/webhook-url")"
LESSON_FILE_S3_BUCKET="$(get_param "${PARAMETER_PREFIX}/lesson-file/s3-bucket")"
LESSON_FILE_CLOUDFRONT_DOMAIN="$(get_param "${PARAMETER_PREFIX}/lesson-file/cloudfront-domain")"
LESSON_FILE_CLOUDFRONT_KEY_PAIR_ID="$(get_param "${PARAMETER_PREFIX}/lesson-file/cloudfront-key-pair-id")"
AWS_REGION_VALUE="$(get_param "${PARAMETER_PREFIX}/aws/region")"

required_values=(
  DB_URL
  DB_USERNAME
  DB_PASSWORD
  REDIS_HOST
  REDIS_PORT
  REDIS_PASSWORD
  REDISSON_ADDRESS
  REDISSON_PASSWORD
  JWT_SECRET
  CORS_ALLOWED_ORIGINS
  PORTONE_STORE_ID
  PORTONE_CHANNEL_KEY
  PORTONE_API_SECRET
  INICIS_MID
  INICIS_SIGNKEY
  INICIS_API_KEY
  INICIS_API_IV
  DISCORD_WEBHOOK_URL
  LESSON_FILE_S3_BUCKET
  LESSON_FILE_CLOUDFRONT_DOMAIN
  LESSON_FILE_CLOUDFRONT_KEY_PAIR_ID
  AWS_REGION_VALUE
)

for name in "${required_values[@]}"; do
  require_value "$name" "${!name}"
done

cat > "$ENV_FILE" <<EOF
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=50
DB_URL=$DB_URL
DB_USERNAME=$DB_USERNAME
DB_PASSWORD=$DB_PASSWORD
REDIS_HOST=$REDIS_HOST
REDIS_PORT=$REDIS_PORT
REDIS_PASSWORD=$REDIS_PASSWORD
REDIS_SSL_ENABLED=true
REDISSON_ADDRESS=$REDISSON_ADDRESS
REDISSON_PASSWORD=$REDISSON_PASSWORD
JWT_SECRET=$JWT_SECRET
CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS
PORTONE_STORE_ID=$PORTONE_STORE_ID
PORTONE_CHANNEL_KEY=$PORTONE_CHANNEL_KEY
PORTONE_API_SECRET=$PORTONE_API_SECRET
INICIS_MID=$INICIS_MID
INICIS_SIGNKEY=$INICIS_SIGNKEY
INICIS_API_KEY=$INICIS_API_KEY
INICIS_API_IV=$INICIS_API_IV
DISCORD_WEBHOOK_URL=$DISCORD_WEBHOOK_URL
LESSON_FILE_S3_BUCKET=$LESSON_FILE_S3_BUCKET
LESSON_FILE_CLOUDFRONT_DOMAIN=$LESSON_FILE_CLOUDFRONT_DOMAIN
LESSON_FILE_CLOUDFRONT_KEY_PAIR_ID=$LESSON_FILE_CLOUDFRONT_KEY_PAIR_ID
LESSON_FILE_CLOUDFRONT_PRIVATE_KEY_PATH=$PRIVATE_KEY_FILE
AWS_REGION=$AWS_REGION_VALUE
EOF

get_secret "${PARAMETER_PREFIX}/lesson-file/cloudfront-private-key" > "$PRIVATE_KEY_FILE"
require_value "LESSON_FILE_CLOUDFRONT_PRIVATE_KEY" "$(head -n 1 "$PRIVATE_KEY_FILE")"
chown "$APP_UID:$APP_GID" "$PRIVATE_KEY_FILE"
chmod 600 "$ENV_FILE"
chmod 600 "$PRIVATE_KEY_FILE"
