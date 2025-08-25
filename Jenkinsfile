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
        DOCKER_IMAGE_LATEST = "${PROJECT_NAME}:latest"
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
                        # Build image với tag mới
                        docker build -t ${DOCKER_IMAGE} .
                        docker tag ${DOCKER_IMAGE} ${DOCKER_IMAGE_LATEST}
                        
                        echo "✅ Build Docker image thành công!"
                        docker images | grep ${PROJECT_NAME}
                    """
                }
            }
        }
        
        stage('Deploy Container') {
            steps {
                echo '🚀 Đang deploy container mới...'
                script {
                    sh """
                        # Stop và remove container cũ
                        echo "Dừng container cũ..."
                        docker stop ${PROJECT_NAME} || true
                        docker rm ${PROJECT_NAME} || true
                        
                        # Run container mới với đầy đủ config giống docker-compose
                        echo "Khởi động container mới..."
                        docker run -d \\
                            --name ${PROJECT_NAME} \\
                            --network vangle \\
                            -p 8080:8080 \\
                            --restart unless-stopped \\
                            -e SPRING_PROFILES_ACTIVE=docker \\
                            -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/shopee_affiliate \\
                            -e SPRING_DATASOURCE_USERNAME=postgres \\
                            -e SPRING_DATASOURCE_PASSWORD=postgres \\
                            -e SPRING_JPA_HIBERNATE_DDL_AUTO=none \\
                            -e SPRING_JPA_SHOW_SQL=false \\
                            -e SPRING_LIQUIBASE_ENABLED=true \\
                            -e SPRING_LIQUIBASE_CHANGE_LOG=classpath:db/changelog/db.changelog-master.xml \\
                            -e SPRING_JACKSON_PROPERTY_NAMING_STRATEGY=SNAKE_CASE \\
                            -e SERVER_PORT=8080 \\
                            -e SERVER_SERVLET_CONTEXT_PATH=/api \\
                            -e CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3007,https://shopee.nguocchieuvangle.io.vn/ \\
                            -e JAVA_OPTS="-Xms256m -Xmx512m" \\
                            --link postgres:postgres \\
                            ${DOCKER_IMAGE_LATEST}
                        
                        echo "✅ Deploy thành công!"
                        echo "📍 Ứng dụng đang chạy tại: http://localhost:8080"
                        
                        # Kiểm tra container status
                        docker ps | grep ${PROJECT_NAME}
                        
                        # Kiểm tra network
                        echo "🌐 Network info:"
                        docker network inspect vangle | grep -A 20 "Containers"
                    """
                }
            }
        }
        
        stage('Cleanup Old Images') {
            steps {
                echo '🧹 Dọn dẹp images cũ...'
                script {
                    sh """
                        # Giữ lại 3 images gần nhất
                        docker images | grep ${PROJECT_NAME} | tail -n +4 | awk '{print \$3}' | xargs -r docker rmi -f || true
                        
                        # Xóa dangling images
                        docker image prune -f
                        
                        echo "✅ Đã dọn dẹp images cũ"
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