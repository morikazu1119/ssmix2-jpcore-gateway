FROM nvidia/cuda:12.2.2-cudnn8-devel-ubuntu22.04

# タイムゾーン設定
ENV TZ=Asia/Tokyo

# 引数
ARG UNAME
ARG UID
ARG PROJECT
ARG REQUIREMENTS

# 必要パッケージのインストール & ユーザー作成 & sudo 設定
RUN apt-get update && \
    apt-get install -y \
      sudo git python3 python3-pip python3-venv \
      openssh-server nano openssh-client wget && \
    # グループ・ユーザー作成
    groupadd --gid "${UID}" "${UNAME}" && \
    useradd  --uid "${UID}" --gid "${UID}" --create-home --shell /bin/bash "${UNAME}" && \
    echo "${UNAME}:password" | chpasswd && \
    chage -d 0 "${UNAME}" && \
    # sudo 無パスワード許可
    echo "${UNAME} ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/"${UNAME}" && \
    chmod 440 /etc/sudoers.d/"${UNAME}" && \
    # SSHD ディレクトリ作成
    mkdir /var/run/sshd && \
    # クリーンアップ
    rm -rf /var/lib/apt/lists/*

# SSHホスト鍵の生成
RUN ssh-keygen -A

# SSHD 設定
RUN sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin no/' /etc/ssh/sshd_config

# 作業ディレクトリ
WORKDIR /app

# requirements.txt のコピー
COPY ${REQUIREMENTS} /app/requirements.txt
RUN chown -R ${UNAME}:${UNAME} /app

# ユーザー切り替え
USER ${UNAME}

# Python 仮想環境作成＋依存インストール
RUN python3 -m venv .venv && \
    .venv/bin/pip install --upgrade pip wheel setuptools && \
    .venv/bin/pip install --retries 10 -r /app/requirements.txt

# 仮想環境自動アクティベート設定（UNAME）
RUN echo "source /app/.venv/bin/activate" >> /home/${UNAME}/.bashrc

# ユーザーをROOTに戻す
USER root

# SSH ポート開放
EXPOSE 22

# コンテナ起動時に SSHD をフォアグラウンドで実行
CMD ["/usr/sbin/sshd", "-D"]
