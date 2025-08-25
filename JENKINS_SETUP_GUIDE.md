# 📘 HƯỚNG DẪN TẠO JENKINS JOB CHO SHOPEE-AFFILIATE-BE

## 📋 Mục lục
1. [Đăng nhập Jenkins](#1-đăng-nhập-jenkins)
2. [Cài đặt Plugins cần thiết](#2-cài-đặt-plugins-cần-thiết)
3. [Cấu hình Global Tools](#3-cấu-hình-global-tools)
4. [Cấu hình Email Notification](#4-cấu-hình-email-notification)
5. [Tạo Jenkins Pipeline Job](#5-tạo-jenkins-pipeline-job)
6. [Kiểm tra và Test](#6-kiểm-tra-và-test)

---

## 1. Đăng nhập Jenkins

1. Truy cập: https://jenkins.nguocchieuvangle.io.vn/
2. Nhập username và password của bạn
3. Click **Sign in**

---

## 2. Cài đặt Plugins cần thiết

### Bước 2.1: Vào Plugin Manager
```
Dashboard → Manage Jenkins → Manage Plugins
```

### Bước 2.2: Cài đặt các plugin
1. Click tab **Available**
2. Tìm và tick chọn các plugin sau:
   - ☐ **Git plugin**
   - ☐ **GitHub plugin** 
   - ☐ **Docker Pipeline**
   - ☐ **Email Extension Plugin**
   - ☐ **Pipeline**
   - ☐ **Pipeline: Stage View**

3. Click **Install without restart**
4. Đợi cài đặt xong, tick ☑ **Restart Jenkins when installation is complete**

---

## 3. Cấu hình Global Tools

### Bước 3.1: Vào Global Tool Configuration
```
Dashboard → Manage Jenkins → Global Tool Configuration
```

### Bước 3.2: Cấu hình Maven

1. Scroll xuống mục **Maven**
2. Click **Add Maven**
3. Điền thông tin:
   ```
   Name: Maven-3.9
   ☑ Install automatically
   Version: 3.9.6
   ```
4. Click **Apply**

### Bước 3.3: Cấu hình JDK

1. Scroll lên mục **JDK**
2. Click **Add JDK**
3. Điền thông tin:
   ```
   Name: JDK-21
   ☑ Install automatically
   ☑ I agree to the Java SE Development Kit License Agreement
   Version: jdk-21
   ```
4. Click **Save**

---

## 4. Cấu hình Email Notification

### Bước 4.1: Vào System Configuration
```
Dashboard → Manage Jenkins → Configure System
```

### Bước 4.2: Cấu hình Extended E-mail Notification

1. Scroll xuống tìm **Extended E-mail Notification**
2. Điền thông tin:

   **SMTP Server:**
   ```
   smtp.gmail.com
   ```
   
   **Default user e-mail suffix:**
   ```
   @gmail.com
   ```
   
3. Click **Advanced...**
4. Điền thêm:
   ```
   ☑ Use SMTP Authentication
   User Name: your-email@gmail.com
   Password: your-app-password (*)
   ☑ Use SSL
   SMTP Port: 465
   
   Hoặc:
   ☑ Use TLS
   SMTP Port: 587
   ```
   
   (*) Với Gmail, dùng App Password:
   - Vào Google Account → Security → 2-Step Verification → App passwords
   - Tạo app password cho Jenkins

5. **Default Recipients:** your-email@gmail.com
6. Click **Save**

---

## 5. Tạo Jenkins Pipeline Job

### Bước 5.1: Tạo Job mới
1. Từ Dashboard, click **New Item** (góc trái)
2. Điền thông tin:
   ```
   Enter an item name: shopee-affiliate-be-pipeline
   Type: Pipeline
   ```
3. Click **OK**

### Bước 5.2: Cấu hình General
Trong trang cấu hình job:

1. **Description:**
   ```
   CI/CD Pipeline cho Shopee Affiliate Backend
   Tự động build và deploy khi push code vào branch main
   ```

2. ☑ **GitHub project**
   ```
   Project url: https://github.com/b19cn248/shopee-affiliate-be
   ```

3. ☑ **This project is parameterized** (Tùy chọn)
   Có thể thêm parameters nếu cần

### Bước 5.3: Cấu hình Build Triggers
1. ☑ **GitHub hook trigger for GITScm polling**
   - Cho phép GitHub webhook trigger build

### Bước 5.4: Cấu hình Pipeline

1. **Definition:** Pipeline script from SCM
2. **SCM:** Git
3. **Repositories:**
   - **Repository URL:** 
     ```
     https://github.com/b19cn248/shopee-affiliate-be.git
     ```
   - **Credentials:** 
     - Click **Add** → **Jenkins**
     - Kind: **Username with password**
     - Username: GitHub username của bạn
     - Password: GitHub personal access token
     - ID: `github-credentials`
     - Description: `GitHub Credentials`
     - Click **Add**
     - Chọn credentials vừa tạo

4. **Branches to build:**
   ```
   Branch Specifier: */main
   ```

5. **Script Path:**
   ```
   Jenkinsfile
   ```

6. ☑ **Lightweight checkout** (Tùy chọn, giúp tăng tốc)

### Bước 5.5: Lưu cấu hình
Click **Save**

---

## 6. Kiểm tra và Test

### Bước 6.1: Test build thủ công
1. Trong trang job, click **Build Now**
2. Xem **Build History** → Click #1
3. Click **Console Output** để xem chi tiết

### Bước 6.2: Kiểm tra Stage View
1. Quay lại trang job chính
2. Xem **Stage View** để theo dõi tiến trình từng stage

### Bước 6.3: Troubleshooting

**Lỗi: Cannot find Maven/JDK**
- Kiểm tra tên tools khớp với Jenkinsfile
- Đảm bảo đã cài plugin và cấu hình Global Tools

**Lỗi: Permission denied Docker**
- SSH vào Jenkins server:
  ```bash
  sudo usermod -aG docker jenkins
  sudo systemctl restart jenkins
  ```

**Lỗi: GitHub authentication failed**
- Tạo Personal Access Token trên GitHub:
  - GitHub → Settings → Developer settings → Personal access tokens
  - Scopes cần: repo, admin:repo_hook

---

## 🎯 Checklist hoàn thành

- [ ] Đăng nhập Jenkins thành công
- [ ] Cài đặt tất cả plugins cần thiết
- [ ] Cấu hình Maven và JDK trong Global Tools
- [ ] Cấu hình Email notification
- [ ] Tạo job pipeline thành công
- [ ] Test build thủ công chạy được
- [ ] Nhận được email thông báo

---

## 📝 Lưu ý quan trọng

1. **GitHub Webhook** sẽ được cấu hình riêng trên GitHub
2. **Docker** phải được cài sẵn trên Jenkins server
3. **Email** có thể test bằng cách vào:
   ```
   Manage Jenkins → Configure System → Extended E-mail Notification → Test configuration
   ```

4. **Credentials an toàn:**
   - Không dùng password GitHub thường
   - Dùng Personal Access Token hoặc SSH key
   - Lưu credentials trong Jenkins, không hardcode

---

## 🆘 Cần hỗ trợ?

Nếu gặp lỗi, kiểm tra:
1. Console Output của build
2. System Log: Manage Jenkins → System Log
3. Plugin compatibility
4. Network/Firewall cho webhook
