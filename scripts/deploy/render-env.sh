#!/usr/bin/env bash
set -euo pipefail

ENV_FILE="/opt/devsolve/env/app.env"
PRIVATE_KEY_FILE="/opt/devsolve/env/cloudfront-private-key.pem"

mkdir -p "$(dirname "$ENV_FILE")"

get_param() {
  aws ssm get-parameter \
    --name "$1" \
    --query Parameter.Value \
    --output text
}

get_secret() {
  aws ssm get-parameter \
    --name "$1" \
    --with-decryption \
    --query Parameter.Value \
    --output text
}

get_rds_password() {
  local secret_arn

  secret_arn="$(get_param /devsolve/dev/db/master-secret-arn)"
  aws secretsmanager get-secret-value \
    --secret-id "$secret_arn" \
    --query SecretString \
    --output text \
    | jq -r '.password'
}

cat > "$ENV_FILE" <<EOF
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=50
DB_URL=$(get_param /devsolve/dev/db/url)
DB_USERNAME=$(get_param /devsolve/dev/db/username)
DB_PASSWORD=$(get_rds_password)
REDIS_HOST=$(get_param /devsolve/dev/redis/host)
REDIS_PORT=$(get_param /devsolve/dev/redis/port)
REDIS_PASSWORD=$(get_secret /devsolve/dev/redis/password)
REDIS_SSL_ENABLED=true
REDISSON_ADDRESS=rediss://$(get_param /devsolve/dev/redis/host):$(get_param /devsolve/dev/redis/port)
REDISSON_PASSWORD=$(get_secret /devsolve/dev/redis/password)
JWT_SECRET=$(get_secret /devsolve/dev/jwt/secret)
CORS_ALLOWED_ORIGINS=$(get_param /devsolve/dev/cors/allowed-origins)
PORTONE_STORE_ID=$(get_param /devsolve/dev/payment/portone-store-id)
PORTONE_CHANNEL_KEY=$(get_secret /devsolve/dev/payment/portone-channel-key)
PORTONE_API_SECRET=$(get_secret /devsolve/dev/payment/portone-api-secret)
INICIS_MID=$(get_param /devsolve/dev/payment/inicis-mid)
INICIS_SIGNKEY=$(get_secret /devsolve/dev/payment/inicis-signkey)
INICIS_API_KEY=$(get_secret /devsolve/dev/payment/inicis-api-key)
INICIS_API_IV=$(get_secret /devsolve/dev/payment/inicis-api-iv)
DISCORD_WEBHOOK_URL=$(get_secret /devsolve/dev/discord/webhook-url)
LESSON_FILE_S3_BUCKET=$(get_param /devsolve/dev/lesson-file/s3-bucket)
LESSON_FILE_CLOUDFRONT_DOMAIN=$(get_param /devsolve/dev/lesson-file/cloudfront-domain)
LESSON_FILE_CLOUDFRONT_KEY_PAIR_ID=$(get_param /devsolve/dev/lesson-file/cloudfront-key-pair-id)
LESSON_FILE_CLOUDFRONT_PRIVATE_KEY_PATH=$PRIVATE_KEY_FILE
AWS_REGION=$(get_param /devsolve/dev/aws/region)
EOF

get_secret /devsolve/dev/lesson-file/cloudfront-private-key > "$PRIVATE_KEY_FILE"
chmod 600 "$ENV_FILE"
chmod 600 "$PRIVATE_KEY_FILE"
