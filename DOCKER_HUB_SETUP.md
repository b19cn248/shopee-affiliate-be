# 🐳 Hướng dẫn sử dụng Docker Hub cho CI/CD

## 📋 Tổng quan
Jenkins (Docker) → Build image → Push Docker Hub → Pull & Run trên cùng VPS

## 🔧 Bước 1: Tạo tài khoản Docker Hub

1. Truy cập https://hub.docker.com/
2. Sign up (miễn phí)
3. Create Repository:
   - Repository name: `shopee-affiliate-be`
   - Visibility: Public (hoặc Private nếu muốn)
   - Namespace: `yourusername/shopee-affiliate-be`

## 🔧 Bước 2: Cấu hình Docker Hub Credentials trong Jenkins

### 2.1. Tạo Access Token trên Docker Hub
1. Docker Hub → Account Settings → Security
2. New Access Token:
   - Token description: `jenkins-token`
   - Access permissions: `Read, Write, Delete`
3. Copy token (chỉ hiện 1 lần!)

### 2.2. Thêm Credentials trong Jenkins
1. Jenkins → Manage Jenkins → Manage Credentials
2. Add Credentials:
   ```
   Kind: Username with password
   Username: your-dockerhub-username
   Password: paste-access-token-here
   ID: dockerhub-credentials
   Description: Docker Hub Credentials
   ```

## 🔧 Bước 3: Mount Docker Socket cho Jenkins Container

### 3.1. Kiểm tra Jenkins container hiện tại
```bash
docker ps | grep jenkins
docker inspect jenkins_container_name
```

### 3.2. Update docker-compose.yml hoặc restart với mount
```yaml
# docker-compose.yml
version: '3'
services:
  jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins
    ports:
      - "8080:8080"
    volumes:
      - jenkins_home:/var/jenkins_home
      - /var/run/docker.sock:/var/run/docker.sock  # Mount Docker socket
      - /usr/bin/docker:/usr/bin/docker            # Mount Docker CLI
    environment:
      - DOCKER_HOST=unix:///var/run/docker.sock
    user: root  # Hoặc add jenkins user vào docker group

volumes:
  jenkins_home:
```

Hoặc nếu run bằng docker run:
```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/bin/docker:/usr/bin/docker \
  --user root \
  jenkins/jenkins:lts
```

### 3.3. Fix permissions (nếu cần)
```bash
# Trong Jenkins container
docker exec -it jenkins bash
chmod 666 /var/run/docker.sock
# hoặc
groupadd docker
usermod -aG docker jenkins
```

## 🔧 Bước 4: Update Jenkinsfile cho Docker Hub

```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_HUB_REPO = 'yourusername/shopee-affiliate-be'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        // ... các stages build Maven giữ nguyên ...
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_HUB_REPO}:${DOCKER_TAG}")
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub-credentials') {
                        docker.image("${DOCKER_HUB_REPO}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_HUB_REPO}:${DOCKER_TAG}").push('latest')
                    }
                }
            }
        }
        
        stage('Deploy on Same VPS') {
            steps {
                sh """
                    # Stop old container
                    docker stop shopee-affiliate-be || true
                    docker rm shopee-affiliate-be || true
                    
                    # Pull latest image
                    docker pull ${DOCKER_HUB_REPO}:latest
                    
                    # Run new container
                    docker run -d \
                        --name shopee-affiliate-be \
                        -p 8080:8080 \
                        --restart unless-stopped \
                        ${DOCKER_HUB_REPO}:latest
                    
                    # Clean up old images
                    docker image prune -f
                """
            }
        }
    }
}
```

## 🎯 Workflow hoàn chỉnh

1. **Developer push code** → GitHub
2. **GitHub webhook** → Trigger Jenkins
3. **Jenkins build:**
   - Checkout code
   - Build với Maven
   - Build Docker image
   - Push lên Docker Hub
   - Pull image từ Docker Hub
   - Deploy container mới trên cùng VPS
   - Clean up images cũ

## 💡 Lưu ý quan trọng

1. **Docker trong Docker**: Jenkins container cần access Docker daemon của host
2. **Security**: Dùng access token thay vì password
3. **Tags**: Luôn tag với build number + latest
4. **Cleanup**: Xóa images cũ để tiết kiệm dung lượng

## 🚀 Test thử

```bash
# Kiểm tra Jenkins có thể dùng Docker không
docker exec -it jenkins_container docker ps

# Nếu lỗi, kiểm tra mount
docker exec -it jenkins_container ls -la /var/run/docker.sock
```

## 🔒 Bảo mật

- Dùng private repository nếu code nhạy cảm
- Rotate access tokens định kỳ
- Giới hạn quyền token (chỉ cần read/write)
- Không expose port không cần thiết