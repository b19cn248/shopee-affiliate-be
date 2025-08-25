#!/bin/bash

echo "🔍 Debug Database Connection Issues"

echo "1. 🌐 Kiểm tra Docker networks:"
docker network ls

echo -e "\n2. 📋 Kiểm tra containers trong network 'vangle':"
docker network inspect vangle

echo -e "\n3. 🐘 Kiểm tra PostgreSQL container:"
POSTGRES_CONTAINER=$(docker ps --filter "name=postgres" --format "{{.Names}}" | head -1)
if [ -n "$POSTGRES_CONTAINER" ]; then
    echo "✅ PostgreSQL container: $POSTGRES_CONTAINER"
    docker ps | grep postgres
    echo "Status: $(docker inspect -f '{{.State.Status}}' $POSTGRES_CONTAINER)"
    echo "Networks: $(docker inspect -f '{{range $net, $config := .NetworkSettings.Networks}}{{$net}} {{end}}' $POSTGRES_CONTAINER)"
else
    echo "❌ PostgreSQL container không tìm thấy!"
fi

echo -e "\n4. 🚀 Kiểm tra Shopee app container:"
APP_CONTAINER=$(docker ps --filter "name=shopee-affiliate-be" --format "{{.Names}}" | head -1)
if [ -n "$APP_CONTAINER" ]; then
    echo "✅ App container: $APP_CONTAINER"
    docker ps | grep shopee-affiliate-be
    echo "Status: $(docker inspect -f '{{.State.Status}}' $APP_CONTAINER)"
    echo "Networks: $(docker inspect -f '{{range $net, $config := .NetworkSettings.Networks}}{{$net}} {{end}}' $APP_CONTAINER)"
    
    echo -e "\n📋 Environment variables:"
    docker exec $APP_CONTAINER env | grep -E "(SPRING_|SERVER_|JAVA_)"
    
    echo -e "\n📋 Logs (last 20 lines):"
    docker logs --tail 20 $APP_CONTAINER
else
    echo "❌ App container không tìm thấy!"
fi

echo -e "\n5. 🧪 Test kết nối network:"
if [ -n "$APP_CONTAINER" ] && [ -n "$POSTGRES_CONTAINER" ]; then
    echo "Test ping từ app container đến postgres:"
    docker exec $APP_CONTAINER ping -c 3 postgres || echo "❌ Không ping được postgres"
    
    echo "Test DNS resolution:"
    docker exec $APP_CONTAINER nslookup postgres || echo "❌ Không resolve được postgres hostname"
    
    echo "Test port 5432:"
    docker exec $APP_CONTAINER nc -zv postgres 5432 || echo "❌ Port 5432 không accessible"
fi

echo -e "\n6. 💡 Kiểm tra docker-compose configuration:"
if [ -f "docker-compose.yml" ]; then
    echo "Docker-compose networks:"
    grep -A 5 "networks:" docker-compose.yml
    
    echo -e "\nDocker-compose database config:"
    grep -A 10 "SPRING_DATASOURCE" docker-compose.yml
else
    echo "❌ Không tìm thấy docker-compose.yml"
fi

echo -e "\n7. 🔧 Gợi ý fix:"
echo "- Đảm bảo cả app và postgres đều trong network 'vangle'"
echo "- Kiểm tra env vars SPRING_DATASOURCE_URL có đúng hostname 'postgres' không"
echo "- Restart postgres container nếu cần: docker restart \$POSTGRES_CONTAINER"
echo "- Chạy lại Jenkins deploy với network config đầy đủ"