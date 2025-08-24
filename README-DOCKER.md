# Docker Setup for Shopee Affiliate Backend

## Prerequisites
- Docker và Docker Compose đã được cài đặt
- PostgreSQL đang chạy trên host machine (port 5435)
- Database `shopee_affiliate` đã được tạo

## Quick Start

### 1. Build và chạy với Docker Compose

```bash
# Build image và start service
docker-compose up -d --build

# Xem logs
docker-compose logs -f shopee-affiliate-be

# Stop service
docker-compose down
```

### 2. Chỉ build Docker image

```bash
# Build image
docker build -t shopee-affiliate-be .

# Run container
docker run -d \
  --name shopee-affiliate-be \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5435/shopee_affiliate \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  --add-host=host.docker.internal:host-gateway \
  shopee-affiliate-be
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SPRING_DATASOURCE_URL | PostgreSQL connection URL | jdbc:postgresql://host.docker.internal:5435/shopee_affiliate |
| SPRING_DATASOURCE_USERNAME | Database username | postgres |
| SPRING_DATASOURCE_PASSWORD | Database password | postgres |
| SPRING_PROFILES_ACTIVE | Spring profile | docker |
| SERVER_PORT | Application port | 8080 |
| CORS_ALLOWED_ORIGINS | CORS allowed origins | http://localhost:3000 |

## Troubleshooting

### Không kết nối được PostgreSQL
- Kiểm tra PostgreSQL đang chạy: `docker ps | grep postgres`
- Kiểm tra port 5435 đang mở: `netstat -an | grep 5435`
- Thử ping từ container: `docker exec shopee-affiliate-be ping host.docker.internal`

### Liquibase migration fails
- Kiểm tra database `shopee_affiliate` đã tồn tại
- Xem chi tiết lỗi: `docker logs shopee-affiliate-be`
- Reset Liquibase: Xóa bảng `databasechangelog` và `databasechangeloglock`

### Application không start
- Kiểm tra memory: `docker stats`
- Tăng memory limit trong docker-compose.yml nếu cần
- Kiểm tra Java version: `docker exec shopee-affiliate-be java -version`

## API Testing

```bash
# Health check
curl http://localhost:8080/api/actuator/health

# Get vouchers
curl http://localhost:8080/api/v1/vouchers

# Swagger UI
open http://localhost:8080/api/swagger-ui.html
```