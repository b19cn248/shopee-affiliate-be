# 🔧 Debug Jenkins Job Configuration

## 📋 Kiểm tra cấu hình Job

1. **Vào Jenkins Job Configuration:**
   - Jenkins Dashboard → Job Name → Configure

2. **Kiểm tra Pipeline Definition:**
   ```
   Definition: Pipeline script from SCM ✅
   SCM: Git ✅
   Repository URL: https://github.com/b19cn248/shopee-affiliate-be.git ✅
   Branch: */main ✅
   Script Path: Jenkinsfile ✅
   ```

3. **Thêm Lightweight checkout:**
   - Scroll xuống tìm mục "Pipeline"
   - ☑ **Lightweight checkout** (QUAN TRỌNG!)

## 🔧 Nguyên nhân & Giải pháp

### Nguyên nhân:
- Jenkins cố gắng đọc Jenkinsfile trước khi clone repository
- Workspace chưa có Git repository
- Git command fail vì "not in git directory"

### Giải pháp 1: Enable Lightweight Checkout
1. Job Configuration → Pipeline → ☑ Lightweight checkout
2. Save

### Giải pháp 2: Sử dụng Pipeline Script trực tiếp
1. Definition: **Pipeline script** (thay vì from SCM)
2. Copy-paste nội dung Jenkinsfile vào Script box
3. Save

### Giải pháp 3: Fix Git trong Jenkins Container
```bash
# SSH vào Jenkins container
docker exec -it jenkins bash

# Kiểm tra Git
git --version

# Tạo temp repo để test
cd /tmp
git init test-repo
cd test-repo
git remote add origin https://github.com/b19cn248/shopee-affiliate-be.git

# Test command bị lỗi
git config remote.origin.url https://github.com/b19cn248/shopee-affiliate-be.git
```

## 🎯 Khuyến nghị

**Cách nhanh nhất:** Sử dụng **Lightweight checkout** trong Pipeline configuration.

Nếu vẫn lỗi, dùng **Pipeline script** thay vì **Pipeline script from SCM**.