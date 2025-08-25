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
        
        stage('Build Docker Image') {
            steps {
                echo '🐳 Đang build Docker image...'
                script {
                    sh """
                        docker build -t ${DOCKER_IMAGE} .
                        echo "✅ Build Docker image thành công!"
                        
                        # Liệt kê image vừa build
                        docker images | grep ${PROJECT_NAME}
                    """
                }
            }
        }
        
        stage('Deploy Local') {
            steps {
                echo '🚀 Đang deploy ứng dụng...'
                script {
                    sh """
                        # Dừng container cũ nếu đang chạy
                        docker stop ${PROJECT_NAME} || true
                        docker rm ${PROJECT_NAME} || true
                        
                        # Chạy container mới
                        docker run -d \
                            --name ${PROJECT_NAME} \
                            -p 8080:8080 \
                            --restart unless-stopped \
                            ${DOCKER_IMAGE}
                        
                        echo "✅ Deploy thành công!"
                        echo "📍 Ứng dụng đang chạy tại: http://localhost:8080"
                        
                        # Kiểm tra container status
                        docker ps | grep ${PROJECT_NAME}
                    """
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo '🏥 Kiểm tra ứng dụng...'
                script {
                    sh """
                        # Đợi 10 giây để ứng dụng khởi động
                        sleep 10
                        
                        # Kiểm tra container còn chạy không
                        if docker ps | grep -q ${PROJECT_NAME}; then
                            echo "✅ Container đang chạy bình thường"
                        else
                            echo "❌ Container không chạy!"
                            exit 1
                        fi
                        
                        # Kiểm tra logs
                        echo "📋 10 dòng log cuối:"
                        docker logs --tail 10 ${PROJECT_NAME}
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline thành công!'
            emailext (
                subject: "✅ Build Thành Công: ${PROJECT_NAME} - Build #${BUILD_NUMBER}",
                body: """
                    <h2>Build Thành Công!</h2>
                    <p><b>Project:</b> ${PROJECT_NAME}</p>
                    <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                    <p><b>Branch:</b> ${env.BRANCH_NAME ?: 'main'}</p>
                    <p><b>Thời gian:</b> ${currentBuild.durationString}</p>
                    <hr>
                    <p>Xem chi tiết tại: <a href="${BUILD_URL}">${BUILD_URL}</a></p>
                """,
                to: '${DEFAULT_RECIPIENTS}',
                mimeType: 'text/html'
            )
        }
        
        failure {
            echo '❌ Pipeline thất bại!'
            emailext (
                subject: "❌ Build Thất Bại: ${PROJECT_NAME} - Build #${BUILD_NUMBER}",
                body: """
                    <h2>Build Thất Bại!</h2>
                    <p><b>Project:</b> ${PROJECT_NAME}</p>
                    <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                    <p><b>Branch:</b> ${env.BRANCH_NAME ?: 'main'}</p>
                    <p><b>Thời gian:</b> ${currentBuild.durationString}</p>
                    <hr>
                    <p><b>Lỗi:</b></p>
                    <pre>${currentBuild.currentResult}</pre>
                    <hr>
                    <p>Xem chi tiết tại: <a href="${BUILD_URL}">${BUILD_URL}</a></p>
                """,
                to: '${DEFAULT_RECIPIENTS}',
                mimeType: 'text/html'
            )
        }
        
        always {
            echo '🧹 Dọn dẹp workspace...'
            cleanWs()
        }
    }
}