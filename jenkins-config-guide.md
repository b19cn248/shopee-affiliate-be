# 🔧 Hướng dẫn cấu hình Jenkins Job - Fix lỗi Git

## 📋 Các bước thực hiện

### 1. Vào cấu hình Jenkins Job
```
Jenkins Dashboard → Job Name → Configure
```

### 2. Cấu hình Pipeline
**TRƯỚC (Gây lỗi):**
```
Definition: Pipeline script from SCM
SCM: Git
Repository URL: https://github.com/b19cn248/shopee-affiliate-be.git
Script Path: Jenkinsfile
```

**SAU (Fix lỗi):**
```
Definition: Pipeline script
Script: [Copy nội dung từ jenkinsfile-content.txt]
```

### 3. Cấu hình Build Triggers (Tùy chọn)
```
☑ GitHub hook trigger for GITScm polling
```

### 4. Save và Test
- Click **Save**
- Click **Build Now**

## 🎯 Tại sao cách này fix được lỗi?

### Vấn đề cũ:
1. Jenkins cố đọc Jenkinsfile từ SCM
2. Tạo Git workspace để fetch Jenkinsfile
3. **BỊ FAIL** tại bước git config (chưa có repo)

### Giải pháp mới:
1. Pipeline script được embed trực tiếp trong Jenkins
2. Không cần đọc từ SCM trước
3. Git checkout chỉ chạy TRONG stage 'Checkout Code'
4. **THÀNH CÔNG** vì có workspace đầy đủ

## 🔄 Workflow mới:
1. User trigger build
2. Jenkins load pipeline script (từ config, không phải SCM)
3. Execute stage 'Checkout Code' → Clone repo
4. Execute các stages khác với code đã clone
5. Success! 🎉

## 💡 Lưu ý
- Mỗi khi sửa Jenkinsfile trong repo, phải copy lại vào Jenkins config
- Hoặc sau này có thể chuyển lại SCM khi fix được Git issue
- Hiện tại cách này ổn định 100%