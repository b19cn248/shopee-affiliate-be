# 🚀 Hướng dẫn Deploy từ Jenkins đến VPS

## 📋 Tổng quan
Jenkins build code → Copy file qua SSH → Deploy trên VPS của bạn

## 🔧 Cách 1: Sử dụng SSH Key

### Bước 1: Tạo SSH Key trên Jenkins
```bash
# SSH vào Jenkins server
sudo -u jenkins ssh-keygen -t rsa -b 4096 -f /var/lib/jenkins/.ssh/id_rsa -N ""
sudo -u jenkins cat /var/lib/jenkins/.ssh/id_rsa.pub
```

### Bước 2: Copy public key lên VPS
```bash
# Trên VPS của bạn
mkdir -p ~/.ssh
echo "paste-public-key-here" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

### Bước 3: Thêm SSH credentials trong Jenkins
1. Manage Jenkins → Manage Credentials
2. Add Credentials:
   - Kind: SSH Username with private key
   - ID: `vps-ssh-key`
   - Username: `your-vps-username`
   - Private Key: Enter directly → Paste private key

### Bước 4: Update Jenkinsfile
```groovy
stage('Deploy to VPS') {
    steps {
        sshagent(['vps-ssh-key']) {
            sh """
                # Copy files to VPS
                scp -o StrictHostKeyChecking=no -r deploy/* user@your-vps-ip:/home/user/shopee-app/
                
                # Build and restart on VPS
                ssh -o StrictHostKeyChecking=no user@your-vps-ip '
                    cd /home/user/shopee-app
                    docker build -t shopee-affiliate-be:latest .
                    docker stop shopee-affiliate-be || true
                    docker rm shopee-affiliate-be || true
                    docker run -d --name shopee-affiliate-be -p 8080:8080 shopee-affiliate-be:latest
                '
            """
        }
    }
}
```

## 🔧 Cách 2: Sử dụng Webhook

### Bước 1: Tạo deploy script trên VPS
```bash
# File: /home/user/deploy-shopee.sh
#!/bin/bash
cd /home/user/shopee-app
git pull origin main
docker build -t shopee-affiliate-be:latest .
docker stop shopee-affiliate-be || true
docker rm shopee-affiliate-be || true
docker run -d --name shopee-affiliate-be -p 8080:8080 shopee-affiliate-be:latest
```

### Bước 2: Tạo webhook endpoint
```bash
# Cài webhook tool
sudo apt-get install webhook

# Config webhook
echo '[
  {
    "id": "deploy-shopee",
    "execute-command": "/home/user/deploy-shopee.sh",
    "command-working-directory": "/home/user"
  }
]' > /etc/webhook.conf

# Run webhook service
webhook -hooks /etc/webhook.conf -verbose -port 9000
```

### Bước 3: Update Jenkinsfile
```groovy
stage('Trigger Deploy') {
    steps {
        sh """
            curl -X POST http://your-vps-ip:9000/hooks/deploy-shopee
        """
    }
}
```

## 🔧 Cách 3: Sử dụng Docker Registry

### Bước 1: Push image lên registry
```groovy
stage('Push to Registry') {
    steps {
        sh """
            docker tag shopee-affiliate-be:${BUILD_NUMBER} your-registry/shopee-affiliate-be:latest
            docker push your-registry/shopee-affiliate-be:latest
        """
    }
}
```

### Bước 2: Pull và run trên VPS
```bash
# Trên VPS
docker pull your-registry/shopee-affiliate-be:latest
docker restart shopee-affiliate-be
```

## 🎯 Khuyến nghị

1. **Dùng SSH Key** - An toàn và dễ setup
2. **Tạo deploy user riêng** trên VPS với quyền hạn chế
3. **Log deployment** để debug dễ hơn
4. **Health check** sau khi deploy

## 🔒 Bảo mật

- KHÔNG commit credentials vào code
- Dùng Jenkins credentials store
- Giới hạn IP access cho webhook
- Rotate SSH keys định kỳ