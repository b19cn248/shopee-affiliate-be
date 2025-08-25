#!/bin/bash

echo "🚀 Script restart Jenkins với Docker support"

# Lấy tên container Jenkins
JENKINS_CONTAINER=$(docker ps --filter "name=jenkins" --format "{{.Names}}" | head -1)

if [ -z "$JENKINS_CONTAINER" ]; then
    echo "❌ Không tìm thấy Jenkins container!"
    exit 1
fi

echo "✅ Jenkins container: $JENKINS_CONTAINER"

# Backup Jenkins data
echo "💾 Backup Jenkins data..."
docker cp $JENKINS_CONTAINER:/var/jenkins_home ./jenkins_backup_$(date +%Y%m%d_%H%M%S)

# Stop container cũ
echo "⏹️  Stop Jenkins container..."
docker stop $JENKINS_CONTAINER
docker rm $JENKINS_CONTAINER

# Lấy Docker group ID
DOCKER_GID=$(stat -c '%g' /var/run/docker.sock)
echo "🔍 Docker group ID: $DOCKER_GID"

# Chạy Jenkins mới với Docker support
echo "🚀 Start Jenkins với Docker support..."
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/bin/docker:/usr/bin/docker \
  --group-add $DOCKER_GID \
  --restart unless-stopped \
  jenkins/jenkins:lts

# Đợi Jenkins khởi động
echo "⏳ Đợi Jenkins khởi động..."
sleep 30

# Test Docker
echo "🧪 Test Docker trong Jenkins..."
docker exec jenkins docker ps

echo "✅ Hoàn thành! Jenkins đã có thể sử dụng Docker"
echo "🌐 Truy cập Jenkins tại: http://your-vps-ip:8080"