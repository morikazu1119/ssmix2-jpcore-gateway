#!/usr/bin/env bash
set -euo pipefail

# 引数のチェック：2～4 引数を許可
if [ "$#" -lt 2 ] || [ "$#" -gt 4 ]; then
  cat <<EOF
Usage: $0 USERNAME PROJECT_NAME [PORT] [CONTAINER_NAME]
  USERNAME       : Docker イメージ名およびコンテナ名のプレフィックス（必須）
  PROJECT_NAME   : プロジェクト名。コンテナ名のサフィックスとしても使われる（必須）
  PORT           : ホスト側のポート番号。省略時は 2222
  CONTAINER_NAME : 起動するコンテナ名。省略時は USERNAME_PROJECT_NAME

Example:
  $0 kazuki llm
  $0 kazuki llm 2022
  $0 kazuki llm 2022 my_llm_container
EOF
  exit 1
fi

# 引数を変数に格納
USERNAME="$1"
PROJECT_NAME="$2"
PORT="${3:-2222}"
CONTAINER_NAME="${4:-${USERNAME}_${PROJECT_NAME}}"

IMAGE_TAG="${USERNAME}_${PROJECT_NAME}"

# マウントパス
HOST_DATA_PATH="/mnt/c/mnt/data"
CONTAINER_DATA_PATH="/home/${USERNAME}/data"
SLUM_VOLUME="slum-volume"
SLUM_MOUNT_PATH="/mnt/slum"

# 起動オプション
GPU_OPTS="--gpus all"
RESTART_OPTS="--restart=always"
SHM_OPTS="--shm-size=64gb"
DETACH_OPTS="--detach"
WORKDIR_OPTS="--workdir=/home/${USERNAME}"
PORT_OPTS="-p ${PORT}:22"

echo "🚀 Starting container '${CONTAINER_NAME}' from image '${IMAGE_TAG}'"
echo "   Host data : ${HOST_DATA_PATH}"
echo "   Mount data: ${CONTAINER_DATA_PATH}"
echo "   Slum vol  : ${SLUM_VOLUME} → ${SLUM_MOUNT_PATH}"

docker run \
  -it \
  ${PORT_OPTS} \
  ${DETACH_OPTS} \
  ${GPU_OPTS} \
  ${RESTART_OPTS} \
  ${SHM_OPTS} \
  --mount type=bind,source="${HOST_DATA_PATH}",target="${CONTAINER_DATA_PATH}" \
  --mount source="${SLUM_VOLUME}",target="${SLUM_MOUNT_PATH}" \
  ${WORKDIR_OPTS} \
  --name "${CONTAINER_NAME}" \
  "${IMAGE_TAG}"

echo "✅ Container '${CONTAINER_NAME}' has been started successfully."
