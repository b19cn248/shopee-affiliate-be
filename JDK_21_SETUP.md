# 🔧 Hướng dẫn cấu hình JDK 21 trong Jenkins

## Cách 1: Sử dụng Oracle Installer (Khuyến nghị)

1. **Click vào dropdown "Add Installer"**

2. **Chọn "Install from java.sun.com"**

3. **Sau khi chọn, sẽ xuất hiện các field mới:**
   - ☑ I agree to the Java SE Development Kit License Agreement
   - Version: Chọn **jdk-21** từ dropdown (nếu có)

## Cách 2: Sử dụng Adoptium/Temurin (Miễn phí)

1. **Click vào dropdown "Add Installer"**

2. **Chọn "Install from adoptium.net"**

3. **Cấu hình:**
   - Version: **jdk-21+35** (hoặc version 21 mới nhất)

## Cách 3: Sử dụng JDK đã cài sẵn trên server

Nếu JDK 21 đã được cài sẵn trên Jenkins server:

1. **BỎ tick "Install automatically"**

2. **Trong field JAVA_HOME, nhập đường dẫn:**
   ```
   /usr/lib/jvm/java-21-openjdk-amd64
   ```
   hoặc
   ```
   /opt/java/jdk-21
   ```
   (Tùy vào nơi JDK được cài đặt)

## Cách 4: Download từ URL

1. **Click "Add Installer"**

2. **Chọn "Extract *.zip/*.tar.gz"**

3. **Nhập URL download JDK 21:**
   ```
   https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz
   ```

## 🔍 Kiểm tra JDK trên server

Nếu không chắc JDK đã cài hay chưa, SSH vào Jenkins server và chạy:

```bash
# Kiểm tra version Java
java -version

# Tìm đường dẫn JDK
which java
readlink -f $(which java)

# Liệt kê các JDK đã cài
ls /usr/lib/jvm/
```

## 💡 Gợi ý

- **Nếu không thấy JDK 21 trong dropdown**: Plugin có thể cần update
- **Khuyến nghị**: Dùng Adoptium/Temurin vì miễn phí và dễ cài
- **Alternative**: Có thể dùng JDK 17 nếu JDK 21 không available

## ✅ Sau khi cấu hình xong

1. Click **Apply** hoặc **Save**
2. Kiểm tra trong Pipeline bằng cách thêm stage test:

```groovy
stage('Check Java Version') {
    steps {
        sh 'java -version'
    }
}
```