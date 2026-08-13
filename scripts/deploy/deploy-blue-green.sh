#!/usr/bin/env bash
set -euo pipefail

IMAGE_URI="${1:?usage: deploy-blue-green.sh <image-uri>}"
COMPOSE_FILE="/opt/devsolve/deploy/docker-compose.yml"
CURRENT_COLOR_FILE="/opt/devsolve/current-color"
LOCK_FILE="/opt/devsolve/deploy.lock"

exec 9>"$LOCK_FILE"
if ! flock -n 9; then
  echo "Another deployment is already running." >&2
  exit 1
fi

notify() {
  /opt/devsolve/bin/notify-discord.sh "$@" || echo "Discord notification failed: $*" >&2
}

login_ecr_if_needed() {
  local registry
  local region

  registry="${IMAGE_URI%%/*}"

  if [[ "$registry" != *.dkr.ecr.*.amazonaws.com ]]; then
    return 0
  fi

  region="$(printf '%s' "$registry" | awk -F. '{print $4}')"
  aws ecr get-login-password --region "$region" \
    | docker login --username AWS --password-stdin "$registry"
}

ACTIVE_COLOR="$(cat "$CURRENT_COLOR_FILE" 2>/dev/null || echo blue)"

case "$ACTIVE_COLOR" in
  blue)
    NEXT_COLOR="green"
    NEXT_SERVICE="app-green"
    OLD_COLOR="blue"
    OLD_SERVICE="app-blue"
    ;;
  green)
    NEXT_COLOR="blue"
    NEXT_SERVICE="app-blue"
    OLD_COLOR="green"
    OLD_SERVICE="app-green"
    ;;
  *)
    echo "Invalid active color: $ACTIVE_COLOR" >&2
    exit 1
    ;;
esac

/opt/devsolve/bin/render-env.sh

export APP_IMAGE="$IMAGE_URI"

login_ecr_if_needed
docker compose -f "$COMPOSE_FILE" pull "$NEXT_SERVICE"
docker compose -f "$COMPOSE_FILE" up -d "$NEXT_SERVICE" nginx

for _ in $(seq 1 30); do
  if docker compose -f "$COMPOSE_FILE" exec -T nginx \
    wget -qO- "http://${NEXT_SERVICE}:8080/actuator/health" >/dev/null; then
    /opt/devsolve/bin/switch-nginx-upstream.sh "$NEXT_COLOR"

    if ! docker compose -f "$COMPOSE_FILE" exec -T nginx \
      wget -qO- "http://127.0.0.1/actuator/health" >/dev/null; then
      /opt/devsolve/bin/switch-nginx-upstream.sh "$OLD_COLOR" || true
      docker compose -f "$COMPOSE_FILE" stop "$NEXT_SERVICE" 2>/dev/null || true
      docker compose -f "$COMPOSE_FILE" rm -f "$NEXT_SERVICE" 2>/dev/null || true
      notify "deploy-failed" "Nginx proxy health check failed for ${IMAGE_URI}"
      exit 1
    fi

    docker compose -f "$COMPOSE_FILE" stop "$OLD_SERVICE" 2>/dev/null || true
    docker compose -f "$COMPOSE_FILE" rm -f "$OLD_SERVICE" 2>/dev/null || true
    notify "deploy-success" "Deployed ${IMAGE_URI} to ${NEXT_COLOR}"
    exit 0
  fi

  sleep 5
done

docker compose -f "$COMPOSE_FILE" logs --tail 100 "$NEXT_SERVICE" || true
docker compose -f "$COMPOSE_FILE" stop "$NEXT_SERVICE" 2>/dev/null || true
docker compose -f "$COMPOSE_FILE" rm -f "$NEXT_SERVICE" 2>/dev/null || true
notify "deploy-failed" "Health check failed for ${IMAGE_URI}"
exit 1
