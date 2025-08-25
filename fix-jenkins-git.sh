#!/bin/bash

echo "🔧 Script fix Jenkins Git issue"

# Tìm Jenkins container
JENKINS_CONTAINER=$(docker ps --filter "name=jenkins" --format "{{.Names}}" | head -1)

if [ -z "$JENKINS_CONTAINER" ]; then
    echo "❌ Không tìm thấy Jenkins container đang chạy!"
    echo "📋 Danh sách containers:"
    docker ps
    exit 1
fi

echo "✅ Jenkins container: $JENKINS_CONTAINER"

echo "🔍 Kiểm tra Git trong Jenkins..."
docker exec $JENKINS_CONTAINER which git || echo "❌ Git không tìm thấy"
docker exec $JENKINS_CONTAINER git --version || echo "❌ Git không hoạt động"

echo "📦 Cài đặt Git và các dependencies..."
docker exec -u root $JENKINS_CONTAINER apt-get update
docker exec -u root $JENKINS_CONTAINER apt-get install -y \
    git \
    curl \
    wget \
    ca-certificates \
    gnupg \
    lsb-release

echo "🧪 Test Git sau khi cài..."
docker exec $JENKINS_CONTAINER git --version

echo "🔧 Cấu hình Git global (tránh lỗi config)..."
docker exec $JENKINS_CONTAINER git config --global user.name "Jenkins CI"
docker exec $JENKINS_CONTAINER git config --global user.email "jenkins@localhost"
docker exec $JENKINS_CONTAINER git config --global init.defaultBranch main

echo "📁 Tạo thư mục workspace nếu chưa có..."
docker exec $JENKINS_CONTAINER mkdir -p /var/jenkins_home/workspace

echo "✅ Hoàn thành! Hãy chạy lại Jenkins job"