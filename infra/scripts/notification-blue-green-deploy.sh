#!/bin/bash
set -euo pipefail

#설정
BASE_DIR="/home/ubuntu/threadly"
INFRA_DIR="$BASE_DIR/infra/notification-service"
LOG_DIR="$BASE_DIR/logs/scripts"
LOG_FILE="$LOG_DIR/notification-blue-green-deploy.log"
ENV_PATH="/home/ubuntu/threadly/infra/notification-service/.env.notification"
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
  local current_port
  current_port=$(grep -Eo 'proxy_pass\s+http://[^:]+:([0-9]+);' "$NGINX_CONF" | awk -F: '{print $NF}' | tr -d ';' | tail -n1)
  [[ -n "${current_port:-}" ]] || { error "NGINX proxy_pass 포트 파싱 실패: $NGINX_CONF"; exit 1; }
  echo "$current_port"
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
  docker compose -f "$f" -p "$slot" up -d --build
}

compose_down(){
  local slot="$1"
  local f; f="$(compose_file_for "$slot")"
  warn "docker compose down ($slot): $f"
  docker compose -f "$f" -p "$slot" down || true
  warn "버전: $slot 종료 됨"
}

change_env_app_version(){
  if grep -q "^APP_VERSION=" "$ENV_PATH"; then
    sudo sed -i "s/^APP_VERSION=.*/APP_VERSION=$NEXT/" "$ENV_PATH"
  else
    echo "APP_VERSION=$NEXT" | tee -a "$ENV_PATH" > /dev/null
  fi
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

nginx_port_change(){
  local current_port="$1"
  local next_port="$2"

  log "Nginx 포트 전환 중..."
  sudo sed -i "s/$current_port/$next_port/" "$NGINX_CONF"
  sudo nginx -s reload
  log "Nginx Reload 완료"
}

log "======Deploy start======"
CURRENT_PORT="$(parse_current_port)"

if [ "$CURRENT_PORT" = "8082" ]; then
  CURRENT="blue"
  NEXT="green"
  CURRENT_PORT="8082"
  NEXT_PORT="8083"
else
  CURRENT="green"
  NEXT="blue"
  CURRENT_PORT="8083"
  NEXT_PORT="8082"
fi

log "현재 버전: $CURRENT ($CURRENT_PORT), 배포할 버전: $NEXT ($NEXT_PORT)"

# .env APP_VERSION 변경
change_env_app_version

# 다음 버전 실행
log "새로운 버전($NEXT) 실행 중..."
compose_up "$NEXT"
sleep 10

# Health Check
log "Health Check 시작..."
if ! health_check "$NEXT_PORT"; then
  error "Health Check 실패 -> 롤백 수행"
  exit 1;
fi

# Nginx 포트 전환
nginx_port_change "$CURRENT_PORT" "$NEXT_PORT"

# 기존 버전 종료
log "기존 버전($CURRENT) 종료 중..."
compose_down "$CURRENT"

log "======Deploy Finish======"
