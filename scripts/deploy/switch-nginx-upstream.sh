#!/usr/bin/env bash
set -euo pipefail

COLOR="${1:?usage: switch-nginx-upstream.sh <blue|green>}"
COMPOSE_FILE="/opt/devsolve/deploy/docker-compose.yml"
NGINX_CONF="/opt/devsolve/deploy/nginx/upstream-active.conf"
BACKUP_CONF="$(mktemp)"
HAS_BACKUP=false

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

if [[ -f "$NGINX_CONF" ]]; then
  cp "$NGINX_CONF" "$BACKUP_CONF"
  HAS_BACKUP=true
fi

restore_nginx_conf() {
  if [[ "$HAS_BACKUP" == "true" ]]; then
    cp "$BACKUP_CONF" "$NGINX_CONF"
  else
    rm -f "$NGINX_CONF"
  fi

  rm -f "$BACKUP_CONF"
}

cat > "$NGINX_CONF" <<EOF
set \$backend_upstream http://${TARGET}:8080;
EOF

if ! docker compose -f "$COMPOSE_FILE" exec -T nginx nginx -t; then
  restore_nginx_conf
  docker compose -f "$COMPOSE_FILE" exec -T nginx nginx -s reload || true
  exit 1
fi

if ! docker compose -f "$COMPOSE_FILE" exec -T nginx nginx -s reload; then
  restore_nginx_conf
  docker compose -f "$COMPOSE_FILE" exec -T nginx nginx -s reload || true
  exit 1
fi

rm -f "$BACKUP_CONF"
echo "$COLOR" > /opt/devsolve/current-color
