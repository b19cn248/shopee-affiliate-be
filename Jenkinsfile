pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'  // Tên Maven đã cấu hình trong Jenkins
        jdk 'JDK-21'       // Tên JDK đã cấu hình trong Jenkins
    }
    
    environment {
        // Biến môi trường
        PROJECT_NAME = 'shopee-affiliate-be'
        DOCKER_HUB_REPO = 'yourdockerhubusername/shopee-affiliate-be' // TODO: Thay username của bạn
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_IMAGE_LATEST = "${DOCKER_HUB_REPO}:latest"
        DOCKER_IMAGE_TAGGED = "${DOCKER_HUB_REPO}:${DOCKER_TAG}"
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
                    // Build image với tag
                    sh """
                        docker build -t ${DOCKER_IMAGE_TAGGED} .
                        docker tag ${DOCKER_IMAGE_TAGGED} ${DOCKER_IMAGE_LATEST}
                        echo "✅ Build Docker image thành công!"
                    """
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                echo '📤 Đang push image lên Docker Hub...'
                script {
                    // Login và push lên Docker Hub
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                            docker push ${DOCKER_IMAGE_TAGGED}
                            docker push ${DOCKER_IMAGE_LATEST}
                            echo "✅ Push Docker image thành công!"
                        """
                    }
                }
            }
        }
        
        stage('Deploy on VPS') {
            steps {
                echo '🚀 Đang deploy container mới...'
                script {
                    sh """
                        # Stop và remove container cũ
                        docker stop ${PROJECT_NAME} || true
                        docker rm ${PROJECT_NAME} || true
                        
                        # Pull image mới từ Docker Hub
                        docker pull ${DOCKER_IMAGE_LATEST}
                        
                        # Run container mới
                        docker run -d \\
                            --name ${PROJECT_NAME} \\
                            -p 8080:8080 \\
                            --restart unless-stopped \\
                            ${DOCKER_IMAGE_LATEST}
                        
                        echo "✅ Deploy thành công!"
                        
                        # Kiểm tra container status
                        docker ps | grep ${PROJECT_NAME}
                        
                        # Clean up old images
                        docker image prune -af --filter "until=24h"
                    """
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo '🏥 Kiểm tra ứng dụng...'
                script {
                    sh """
                        # Đợi ứng dụng khởi động
                        echo "Đợi 15 giây để ứng dụng khởi động..."
                        sleep 15
                        
                        # Kiểm tra container
                        if docker ps | grep -q ${PROJECT_NAME}; then
                            echo "✅ Container đang chạy"
                            
                            # Xem logs
                            echo "📋 Logs gần nhất:"
                            docker logs --tail 20 ${PROJECT_NAME}
                        else
                            echo "❌ Container không chạy!"
                            docker logs ${PROJECT_NAME}
                            exit 1
                        fi
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