#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
ENV_FILE="${DEPLOY_DIR}/env/.env"

if [[ ! -f "${ENV_FILE}" ]]; then
  cp "${DEPLOY_DIR}/env/.env.example" "${ENV_FILE}"
fi

docker compose \
  --env-file "${ENV_FILE}" \
  -f "${DEPLOY_DIR}/docker-compose.yml" \
  up --build -d

