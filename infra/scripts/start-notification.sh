#!/bin/bash
set -euo pipefail

#설정
BASE_DIR="/home/ubuntu/threadly"
INFRA_DIR="$BASE_DIR/infra/notification-service"
LOG_DIR="$BASE_DIR/logs/scripts"
LOG_FILE="$LOG_DIR/start-notification.log"
NGINX_CONF="/etc/nginx/locations/notification.conf"
HEALTH_PATH="/actuator/health"
MAX_RETRIES=10
SLEEP_SEC=10

# 로깅
mkdir -p "$(dirname "$LOG_FILE")"

_ts() { date '+%Y-%m-%d %H:%M:%S'; }
log() { echo "[$(_ts)] [INFO ] $*" | tee -a "$LOG_FILE";}
warn() { echo "[$(_ts)] [WARN ] $*" | tee -a "$LOG_FILE" >&2;}
error() { echo "[$(_ts)] [ERROR ] $*"| tee -a "$LOG_FILE" >&2;}

parse_current_port(){
  local port
  port=$(grep -Eo 'proxy_pass\s+http://[^:]+:([0-9]+);' "$NGINX_CONF" | awk -F: '{print $NF}' | tr -d ';' | tail -n1)
  [[ -n "${port:-}" ]] || { error "NGINX proxy_pass 포트 파싱 실패: $NGINX_CONF"; exit 1; }
  echo "$port"
}

compose_file_for(){
  local slot="$1"
  local f="$INFRA_DIR/docker-compose.${slot}.yml"
  echo "$f"
}

compose_up(){
  local slot="$1"
  local f; f="$(compose_file_for "$slot")"
  log "docker compose up -d ($slot): $f"
  docker compose -f "$f" -p "$slot" up -d
}

compose_down(){
  local slot="$1"
  local f; f="$(compose_file_for "$slot")"
  warn "docker compose down ($slot): $f"
  docker compose -f "$f" -p "$slot" down || true
}

health_check(){
local port="$1"
local url="http://localhost:${port}${HEALTH_PATH}"

log "Health Check 시작..."

local i
for((i=1; i<=MAX_RETRIES; i++)); do
  if curl -fsS "$url" >/dev/null 2>&1; then
    log "Health check 성공!"
    return 0
  fi
  log "재시도.."
  sleep "$SLEEP_SEC"
done
return 1
}

log "======Threadly Service Start======"

CURRENT_PORT="$(parse_current_port)"
case "$CURRENT_PORT" in
  8082) SLOT="blue" ;;
  8083) SLOT="green" ;;
  *)    error "지원하지 않는 포트: $CURRENT_PORT"; exit 1;;
esac
log "현재 Nginx 대상: $SLOT ($CURRENT_PORT)"

compose_up "$SLOT"

sleep "$SLEEP_SEC"

if ! health_check "$CURRENT_PORT"; then
  error "Health Check 실패 -> 롤백"
  compose_down "$SLOT"
  exit 1
fi

log "Threadly Service 부팅 성공"
log "======Finish======"
