#!/usr/bin/env bash
set -euo pipefail

COLOR="${1:?usage: switch-nginx-upstream.sh <blue|green>}"
COMPOSE_FILE="/opt/devsolve/deploy/docker-compose.yml"
NGINX_CONF="/opt/devsolve/deploy/nginx/upstream-active.conf"

case "$COLOR" in
  blue)
    TARGET="app-blue"
    ;;
  green)
    TARGET="app-green"
    ;;
  *)
    echo "Invalid color: $COLOR" >&2
    exit 1
    ;;
esac

cat > "$NGINX_CONF" <<EOF
set \$backend_upstream http://${TARGET}:8080;
EOF

docker compose -f "$COMPOSE_FILE" exec -T nginx nginx -t
docker compose -f "$COMPOSE_FILE" exec -T nginx nginx -s reload
echo "$COLOR" > /opt/devsolve/current-color
