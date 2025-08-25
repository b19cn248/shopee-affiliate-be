# 🚀 Jenkins + Docker trên cùng VPS - Setup đơn giản

## 📋 Tổng quan
- Jenkins (Docker container) và Spring Boot app chạy trên **CÙNG 1 VPS**
- **KHÔNG CẦN** Docker Hub
- Build và deploy **trực tiếp** trên VPS

## 🔧 Yêu cầu

### 1. Mount Docker socket cho Jenkins container

```bash
# Stop Jenkins hiện tại
docker stop jenkins

# Chạy lại với Docker socket mount
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/bin/docker:/usr/bin/docker \
  --restart unless-stopped \
  jenkins/jenkins:lts
```

Hoặc nếu dùng docker-compose:

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
      - /var/run/docker.sock:/var/run/docker.sock
      - /usr/bin/docker:/usr/bin/docker
    restart: unless-stopped

  shopee-app:
    image: shopee-affiliate-be:latest
    container_name: shopee-affiliate-be
    ports:
      - "8080:8080"
    restart: unless-stopped
    depends_on:
      - postgres  # nếu có

volumes:
  jenkins_home:
```

### 2. Fix permissions (nếu cần)

```bash
# Option 1: Chạy Jenkins với quyền root (đơn giản nhưng ít bảo mật)
docker exec -u root jenkins chmod 666 /var/run/docker.sock

# Option 2: Add jenkins user vào docker group (khuyến nghị)
docker exec -u root jenkins groupadd -f docker
docker exec -u root jenkins usermod -aG docker jenkins
docker restart jenkins
```

## 🎯 Flow hoạt động

1. **Developer push code** → GitHub
2. **GitHub webhook** → Trigger Jenkins
3. **Jenkins trong Docker container:**
   - Pull code từ GitHub
   - Build với Maven
   - Build Docker image (sử dụng Docker của host)
   - Stop container cũ
   - Run container mới
   - Cleanup images cũ
4. **App chạy** trên cùng VPS

## ✅ Ưu điểm

- **Nhanh**: Không cần push/pull từ registry
- **Đơn giản**: Không cần Docker Hub account
- **Tiết kiệm**: Không tốn bandwidth
- **Bảo mật**: Image không public

## 📝 Lưu ý

1. **Disk space**: Monitor dung lượng, images cũ được tự động xóa
2. **Port conflicts**: Đảm bảo ports không bị trùng
3. **Container names**: Sử dụng tên unique cho mỗi service

## 🧪 Test

```bash
# Kiểm tra Jenkins có thể dùng Docker không
docker exec jenkins docker ps

# Xem logs Jenkins
docker logs -f jenkins

# Xem logs app
docker logs -f shopee-affiliate-be
```

## 🔧 Troubleshooting

**Lỗi: Cannot connect to Docker daemon**
```bash
# Kiểm tra Docker socket
ls -la /var/run/docker.sock
# Output: srw-rw---- 1 root docker ...

# Fix permissions
sudo chmod 666 /var/run/docker.sock
```

**Lỗi: docker: command not found**
```bash
# Cài Docker CLI trong Jenkins container
docker exec -u root jenkins apt-get update
docker exec -u root jenkins apt-get install -y docker.io
```