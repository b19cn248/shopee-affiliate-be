# 🔗 Hướng dẫn setup GitHub Webhook tự động trigger Jenkins

## 📋 Bước 1: Cấu hình Jenkins Job

1. **Vào Jenkins job → Configure**
2. **Tìm mục "Build Triggers"**
3. **Tích ☑ "GitHub hook trigger for GITScm polling"**
4. **Save**

## 📋 Bước 2: Thêm Webhook trên GitHub

1. **Truy cập GitHub repository:**
   ```
   https://github.com/b19cn248/shopee-affiliate-be
   ```

2. **Vào Settings → Webhooks → Add webhook**

3. **Điền thông tin:**
   ```
   Payload URL: https://jenkins.nguocchieuvangle.io.vn/github-webhook/
   Content type: application/json
   Secret: (để trống hoặc tạo secret)
   
   Which events would you like to trigger this webhook?
   ☑ Just the push event
   
   ☑ Active
   ```

4. **Click "Add webhook"**

5. **Kiểm tra webhook:**
   - GitHub sẽ gửi test ping
   - Dấu ✅ xanh = thành công
   - Dấu ❌ đỏ = lỗi (check lại URL)

## 📋 Bước 3: Test Auto Trigger

1. **Thay đổi code trong repository**
2. **Commit và push:**
   ```bash
   git add .
   git commit -m "test auto trigger"
   git push origin main
   ```
3. **Kiểm tra Jenkins** - Build sẽ tự động chạy!

## 🔧 Troubleshooting

### Lỗi: Webhook không trigger Jenkins

**Nguyên nhân:**
- URL webhook sai
- Jenkins không accessible từ internet
- Firewall block GitHub IPs

**Giải pháp:**
1. **Kiểm tra URL:**
   ```
   https://jenkins.nguocchieuvangle.io.vn/github-webhook/
   ```
   (Phải có `/github-webhook/` ở cuối)

2. **Test webhook từ browser:**
   ```
   https://jenkins.nguocchieuvangle.io.vn/github-webhook/
   ```
   Response: "Method Not Allowed" = OK
   Response: Timeout/Error = Jenkins không accessible

3. **Kiểm tra nginx config** cho phép POST request

### Lỗi: Build trigger nhưng fail

**Nguyên nhân:** GitHub push event nhưng Jenkins job không có branch context

**Giải pháp:** Trong jenkinsfile-content.txt đã fix với `BRANCH_NAME = "${env.BRANCH_NAME ?: 'main'}"`

## 💡 Lưu ý

- Webhook chỉ trigger khi push lên branch đã cấu hình (main)
- Jenkins phải accessible từ internet
- GitHub webhook IPs: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/about-githubs-ip-addresses

## 🎯 Workflow hoàn chỉnh

1. **Developer push code** → GitHub
2. **GitHub webhook** → Jenkins `/github-webhook/`
3. **Jenkins auto trigger** build job
4. **Pipeline execute:**
   - Checkout code
   - Build Maven
   - Build Docker image
   - Deploy container
   - Health check
5. **Deployment success!** 🎉