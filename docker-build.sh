#!/usr/bin/env bash
set -euo pipefail

print_usage(){
  cat <<EOF
Usage:
  $0 PROJECT_NAME REQUIREMENTS_PATH [USERNAME] [USERID] [DOCKERFILE_PATH] [BUILD_CONTEXT]

Arguments:
  PROJECT_NAME       : イメージ・コンテナ名に使うプロジェクト名（必須）
  REQUIREMENTS_PATH  : requirements.txt のパス（必須）
  USERNAME           : ARG UNAME（省略時は \$USER）
  USERID             : ARG UID（省略時は \$UID）
  DOCKERFILE_PATH    : Dockerfile のパス（省略時は dockerfile）
  BUILD_CONTEXT      : ビルドコンテキスト（省略時は .）
EOF
}

# 必須２つがないとエラー
if [ "$#" -lt 2 ] || [ "$#" -gt 7 ]; then
  print_usage
  exit 1
fi

# 必須２つを先に取り出し
PROJECT_NAME="$1"
REQUIREMENTS_PATH="$2"
shift 2

# 残りを順にオプションとして受け取る
CACHE="${1:-true}"
USERNAME="${2:-$USER}"
USERID="${3:-$UID}"
DOCKERFILE_PATH="${4:-dockerfile}"
BUILD_CONTEXT="${5:-.}"

if [ "$CACHE" = "true" ]; then
  CACHE_OPTS=""
else
  CACHE_OPTS="--no-cache"
fi

IMAGE_TAG="${USERNAME}_${PROJECT_NAME}"

# 存在チェック
for f in "$DOCKERFILE_PATH" "$REQUIREMENTS_PATH"; do
  if [ ! -f "$f" ]; then
    echo "Error: File not found: $f" >&2
    exit 1
  fi
done

cat <<EOF
-----------------------------
Building Docker image:
  PROJECT_NAME       = ${PROJECT_NAME}
  REQUIREMENTS_PATH  = ${REQUIREMENTS_PATH}
  USERNAME           = ${USERNAME}
  USERID             = ${USERID}
  DOCKERFILE_PATH    = ${DOCKERFILE_PATH}
  BUILD_CONTEXT      = ${BUILD_CONTEXT}
  IMAGE_TAG           = ${IMAGE_TAG}
-----------------------------
EOF

echo "🔨 Building Docker image '${IMAGE_TAG}' from '${DOCKERFILE_PATH}'"

docker build \
  ${CACHE_OPTS} \
  --file "${DOCKERFILE_PATH}" \
  --tag  "${IMAGE_TAG}" \
  --build-arg UNAME="${USERNAME}" \
  --build-arg UID="${USERID}" \
  --build-arg PROJECT="${PROJECT_NAME}" \
  --build-arg REQUIREMENTS="${REQUIREMENTS_PATH}" \
  "${BUILD_CONTEXT}"

echo "✅ Built image: ${IMAGE_TAG}"
