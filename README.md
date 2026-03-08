# テンプレートリポジトリ

## 概要

このリポジトリは、Dockerを使用したPython開発環境のテンプレートです。
再現性の高い開発環境を迅速に構築することを目的としています。

主な特徴:
- DockerおよびDocker Composeによる環境構築
- Pythonの静的解析ツールを標準で導入
  - Linter: [Ruff](https://github.com/astral-sh/ruff)
  - Formatter: [Black](https://github.com/psf/black)
  - Type Checker: [Mypy](http://mypy-lang.org/)
- VSCodeでの開発を想定した設定ファイル同梱
- `pre-commit`によるコード品質の自動チェック

---

## セットアップ

### 前提条件
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/) (推奨)
- `bash`互換シェル

### 手順
1. このリポジトリをクローンまたはテンプレートとして使用します。
   ```bash
   git clone https://github.com/your_username/your_repository.git
   cd your_repository
   ```

2. `pre-commit`フックをセットアップします。(推奨)
   ```bash
   pip install pre-commit
   pre-commit install
   ```

---

## コンテナの利用方法

コンテナのビルドと実行には、2つの方法があります。

### 方法1: シェルスクリプトを利用する

`docker-build.sh`と`docker-run.sh`スクリプトを使って、手動でイメージのビルドとコンテナの起動を行います。

#### 1. イメージのビルド
`docker-build.sh` を実行して、Dockerイメージをビルドします。

**書式:**
```bash
./docker-build.sh <プロジェクト名> <requirements.txtのパス> [ユーザー名] [ユーザーID]
```

**実行例:**
`sample.txt`の要求仕様で、`my-project`という名前のイメージをビルドします。
```bash
./docker-build.sh my-project requirements/sample.txt
```
これにより、`${USER}_my-project` という名前のDockerイメージが作成されます。

#### 2. コンテナの起動
`docker-run.sh` を実行して、ビルドしたイメージからコンテナを起動します。

**書式:**
```bash
./docker-run.sh <ユーザー名> <プロジェクト名> [ホスト側ポート] [コンテナ名]
```

**実行例:**
`kazuki`ユーザーで、`my-project`プロジェクトのコンテナを起動します。ホストのポート`2222`をコンテナの`22`番ポートにマッピングします。
```bash
./docker-run.sh kazuki my-project 2222
```
これにより、`kazuki_my-project`という名前のコンテナが起動します。

---

### 方法2: Docker Composeを利用する (推奨)

`docker-compose.yml` を利用して、より簡単にサービスを管理できます。

#### 1. 設定ファイルの作成
プロジェクトのルートに `.env` ファイルを作成し、環境変数を設定します。

**.env ファイルの例:**
```env
# --- General ---
TZ=Asia/Tokyo

# --- Docker Compose ---
# Service name and container name prefix
COMPOSE_PROJECT_NAME=my-project

# --- Build Arguments ---
# User settings for inside the container
UNAME=kazuki
UID=1000
# Project name used for the image tag (e.g., kazuki_my-project)
PROJECT=my-project
# Path to requirements file
REQUIREMENTS=requirements/sample.txt

# --- Runtime Settings ---
# Host port to map to the container's SSH port (22)
PORT=2222
# Host path to mount as the data directory
HOST_DATA_PATH=./data
```

#### 2. イメージのビルド
`docker-compose build` コマンドでイメージをビルドします。`.env`ファイルの値が自動的に読み込まれます。
```bash
docker-compose build
```

#### 3. コンテナの起動
`docker-compose up` コマンドでコンテナを起動します。
```bash
# フォアグラウンドで起動
docker-compose up

# バックグラウンドで起動
docker-compose up -d
```

#### 4. コンテナの停止・削除
```bash
# 停止
docker-compose down

# ボリュームも削除する場合
docker-compose down -v
```
