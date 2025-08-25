#!/bin/bash

echo "🔍 Kiểm tra Jenkins container hiện tại..."
JENKINS_CONTAINER=$(docker ps --filter "name=jenkins" --format "{{.Names}}" | head -1)

if [ -z "$JENKINS_CONTAINER" ]; then
    echo "❌ Không tìm thấy Jenkins container đang chạy!"
    echo "📋 Danh sách container:"
    docker ps
    exit 1
fi

echo "✅ Tìm thấy Jenkins container: $JENKINS_CONTAINER"

echo -e "\n📊 Kiểm tra volumes đã mount:"
docker inspect $JENKINS_CONTAINER | grep -A 10 "Mounts"

echo -e "\n🔧 Cách 1: Mount Docker socket vào Jenkins container"
echo "----------------------------------------"
echo "# Stop Jenkins hiện tại"
echo "docker stop $JENKINS_CONTAINER"
echo ""
echo "# Backup data (nếu cần)"
echo "docker cp $JENKINS_CONTAINER:/var/jenkins_home ./jenkins_backup"
echo ""
echo "# Chạy lại với Docker socket"
echo "docker run -d \\"
echo "  --name jenkins \\"
echo "  -p 8080:8080 \\"
echo "  -p 50000:50000 \\"
echo "  -v jenkins_home:/var/jenkins_home \\"
echo "  -v /var/run/docker.sock:/var/run/docker.sock \\"
echo "  -v /usr/bin/docker:/usr/bin/docker \\"
echo "  --group-add \$(stat -c '%g' /var/run/docker.sock) \\"
echo "  jenkins/jenkins:lts"

echo -e "\n🔧 Cách 2: Cài Docker CLI trong Jenkins container (tạm thời)"
echo "----------------------------------------"
echo "# Cài Docker CLI"
echo "docker exec -u root $JENKINS_CONTAINER apt-get update"
echo "docker exec -u root $JENKINS_CONTAINER apt-get install -y docker.io"
echo ""
echo "# Fix permissions"
echo "docker exec -u root $JENKINS_CONTAINER chmod 666 /var/run/docker.sock"

echo -e "\n🔧 Cách 3: Sử dụng Docker-in-Docker (DinD)"
echo "----------------------------------------"
echo "docker run -d \\"
echo "  --name jenkins \\"
echo "  --privileged \\"
echo "  -p 8080:8080 \\"
echo "  -v jenkins_home:/var/jenkins_home \\"
echo "  -e DOCKER_HOST=tcp://docker:2376 \\"
echo "  -e DOCKER_CERT_PATH=/certs/client \\"
echo "  -e DOCKER_TLS_VERIFY=1 \\"
echo "  jenkins/jenkins:lts"

echo -e "\n💡 Khuyến nghị: Dùng Cách 1 - Mount Docker socket"
echo "⚠️  Lưu ý: Backup Jenkins data trước khi restart!"