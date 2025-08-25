pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'  // Tên Maven đã cấu hình trong Jenkins
        jdk 'JDK-21'       // Tên JDK đã cấu hình trong Jenkins
    }
    
    environment {
        // Biến môi trường
        PROJECT_NAME = 'shopee-affiliate-be'
        DOCKER_IMAGE = "${PROJECT_NAME}:${BUILD_NUMBER}"
        BRANCH_NAME = "${env.BRANCH_NAME ?: 'main'}"
    }
    
    options {
        // Giữ lại 10 build gần nhất
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Timeout cho toàn bộ pipeline là 30 phút
        timeout(time: 30, unit: 'MINUTES')
    }
    
    triggers {
        // Trigger khi có push vào GitHub
        githubPush()
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                echo '📥 Đang tải code từ GitHub...'
                checkout scm
            }
        }
        
        stage('Build với Maven') {
            steps {
                echo '🔨 Đang build project với Maven...'
                script {
                    try {
                        sh '''
                            echo "Java version:"
                            java -version
                            echo "Maven version:"
                            mvn --version
                            echo "Building project..."
                            mvn clean package -DskipTests
                            echo "✅ Build Maven thành công!"
                        '''
                    } catch (Exception e) {
                        echo "❌ Lỗi khi build: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }
        
        stage('Build JAR file') {
            steps {
                echo '📦 Đang đóng gói JAR file...'
                script {
                    sh """
                        # Kiểm tra file JAR đã được build
                        ls -la target/*.jar
                        
                        # Copy JAR và Dockerfile để chuẩn bị deploy
                        mkdir -p deploy
                        cp target/*.jar deploy/app.jar
                        cp Dockerfile deploy/
                        
                        echo "✅ Chuẩn bị file deploy thành công!"
                    """
                }
            }
        }
        
        stage('Deploy to VPS') {
            steps {
                echo '🚀 Đang deploy lên VPS...'
                script {
                    // CÁCH 1: Dùng SSH Agent (cần cấu hình SSH key trong Jenkins)
                    // sshagent(['your-ssh-credential-id']) {
                    //     sh """
                    //         scp -r deploy/* user@your-vps-ip:/path/to/app/
                    //         ssh user@your-vps-ip 'cd /path/to/app && docker build -t ${DOCKER_IMAGE} . && docker restart ${PROJECT_NAME}'
                    //     """
                    // }
                    
                    // CÁCH 2: Dùng SSH với password (cần plugin SSH)
                    echo """
                    ⚠️  Cần cấu hình thêm để deploy lên VPS:
                    
                    1. Thêm SSH credentials trong Jenkins
                    2. Cài plugin 'SSH Agent' hoặc 'Publish Over SSH'
                    3. Uncomment và cấu hình đoạn code SSH phía trên
                    
                    Hoặc sử dụng webhook để trigger deploy script trên VPS
                    """
                }
            }
        }
        
        stage('Verify Build') {
            steps {
                echo '🏥 Kiểm tra kết quả build...'
                script {
                    sh """
                        echo "📋 File JAR đã build:"
                        ls -lh target/*.jar
                        
                        echo "📁 Nội dung thư mục deploy:"
                        ls -la deploy/
                        
                        echo "✅ Build artifacts đã sẵn sàng để deploy!"
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline thành công!'
            echo "Build #${BUILD_NUMBER} completed successfully"
        }
        
        failure {
            echo '❌ Pipeline thất bại!'
            echo "Build #${BUILD_NUMBER} failed"
        }
        
        always {
            echo '🧹 Dọn dẹp workspace...'
            cleanWs()
        }
    }
}