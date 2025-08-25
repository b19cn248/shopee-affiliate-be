#!/bin/bash

echo "🔧 Fix Jenkins Git Plugin Issue"

# Tìm Jenkins container
JENKINS_CONTAINER=$(docker ps --filter "name=jenkins" --format "{{.Names}}" | head -1)

if [ -z "$JENKINS_CONTAINER" ]; then
    echo "❌ Không tìm thấy Jenkins container!"
    exit 1
fi

echo "✅ Jenkins container: $JENKINS_CONTAINER"

echo "🔧 Fix Jenkins workspace permissions..."
docker exec -u root $JENKINS_CONTAINER mkdir -p /var/jenkins_home/workspace
docker exec -u root $JENKINS_CONTAINER chown -R jenkins:jenkins /var/jenkins_home/workspace
docker exec -u root $JENKINS_CONTAINER chmod -R 755 /var/jenkins_home/workspace

echo "🔧 Init global Git config..."
docker exec $JENKINS_CONTAINER git config --global user.name "Jenkins CI"
docker exec $JENKINS_CONTAINER git config --global user.email "jenkins@localhost"
docker exec $JENKINS_CONTAINER git config --global init.defaultBranch main
docker exec $JENKINS_CONTAINER git config --global safe.directory '*'

echo "🧪 Test Git commands..."
docker exec $JENKINS_CONTAINER sh -c 'cd /tmp && git init test-repo && cd test-repo && git remote add origin https://github.com/b19cn248/shopee-affiliate-be.git && git config remote.origin.url https://github.com/b19cn248/shopee-affiliate-be.git'

if [ $? -eq 0 ]; then
    echo "✅ Git test thành công!"
    echo "💡 Có thể dùng lại 'Pipeline script from SCM'"
else
    echo "❌ Git test thất bại!"
    echo "💡 Khuyến nghị dùng 'Pipeline script' trực tiếp"
fi

# Cleanup
docker exec $JENKINS_CONTAINER rm -rf /tmp/test-repo

echo "🎯 Restart Jenkins để apply changes..."
docker restart $JENKINS_CONTAINER

echo "✅ Hoàn thành! Chờ 30s rồi test lại Jenkins job"