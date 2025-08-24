# Docker Setup for Backend với PostgreSQL có sẵn

## 📋 Prerequisites

Bạn đã có PostgreSQL container đang chạy với cấu hình:
- Container name: `postgres`
- Port: `5435:5432`
- Network: `vangle`
- Username: `postgres`
- Password: `postgres`

## 🚀 Quick Start

### Option 1: Sử dụng script tự động

```bash
cd /mnt/c/Users/Windows/workspace/shopee-affiliate/shopee-affiliate-be

# Cấp quyền execute cho scripts
chmod +x scripts/*.sh

# Start Backend (tự động tạo database và start)
./scripts/start.sh
```

### Option 2: Thủ công step by step

```bash
# 1. Tạo database shopee_affiliate trong PostgreSQL
docker exec -i postgres psql -U postgres -c "CREATE DATABASE shopee_affiliate;"

# 2. Build và start Backend
cd /mnt/c/Users/Windows/workspace/shopee-affiliate/shopee-affiliate-be
docker-compose up -d --build

# 3. Xem logs
docker-compose logs -f
```

## 🔧 Configuration

### Network Configuration
Backend sẽ join vào network `vangle` để kết nối với PostgreSQL container.

### Database Connection
- Host: `postgres` (container name)
- Port: `5432` (internal port)
- Database: `shopee_affiliate`
- Username: `postgres`
- Password: `postgres`

## 📝 Commands

### Start/Stop Backend

```bash
# Start
docker-compose up -d

# Stop
docker-compose down

# Restart
docker-compose restart

# View logs
docker-compose logs -f shopee-affiliate-be
```

### Database Management

```bash
# Access PostgreSQL CLI
docker exec -it postgres psql -U postgres -d shopee_affiliate

# Check tables
docker exec postgres psql -U postgres -d shopee_affiliate -c "\dt"

# View Liquibase changelog
docker exec postgres psql -U postgres -d shopee_affiliate -c "SELECT * FROM databasechangelog;"

# Backup database
docker exec postgres pg_dump -U postgres shopee_affiliate > backup.sql

# Restore database
docker exec -i postgres psql -U postgres shopee_affiliate < backup.sql
```

## 🧪 Testing

### Health Check
```bash
# Backend health
curl http://localhost:8080/api/actuator/health

# Test database connection
docker exec shopee-affiliate-be wget -qO- http://localhost:8080/api/actuator/health | grep -o '"status":"UP"'
```

### API Testing
```bash
# Get all vouchers
curl http://localhost:8080/api/v1/vouchers

# Create test voucher
curl -X POST http://localhost:8080/api/v1/vouchers \
  -H "Content-Type: application/json" \
  -d '{
    "code": "TEST001",
    "title": "Test Voucher",
    "platform": "SHOPEE",
    "discount_type": "PERCENT",
    "discount_value": 10,
    "min_order_amount": 100000,
    "start_at": "2024-01-01T00:00:00",
    "end_at": "2024-12-31T23:59:59"
  }'
```

## 🐛 Troubleshooting

### Cannot connect to PostgreSQL

```bash
# Kiểm tra PostgreSQL container
docker ps | grep postgres

# Kiểm tra network
docker network inspect vangle

# Test connection từ Backend container
docker exec shopee-affiliate-be ping postgres

# Kiểm tra logs của Backend
docker logs shopee-affiliate-be --tail 50
```

### Liquibase errors

```bash
# Reset Liquibase (CẢNH BÁO: sẽ xóa dữ liệu)
docker exec postgres psql -U postgres -d shopee_affiliate -c "
  DROP TABLE IF EXISTS databasechangelog CASCADE;
  DROP TABLE IF EXISTS databasechangeloglock CASCADE;
"

# Restart Backend để chạy lại migration
docker-compose restart shopee-affiliate-be
```

### Port conflicts

```bash
# Kiểm tra port 8080
lsof -i :8080
netstat -tulpn | grep 8080

# Thay đổi port nếu cần (trong docker-compose.yml)
# ports:
#   - "8081:8080"  # Đổi sang port 8081
```

## 📊 Monitoring

### Container stats
```bash
# CPU và Memory usage
docker stats shopee-affiliate-be

# Disk usage
docker system df

# Network info
docker inspect shopee-affiliate-be | grep -A 10 NetworkMode
```

### Application logs
```bash
# All logs
docker-compose logs shopee-affiliate-be

# Last 100 lines
docker-compose logs --tail 100 shopee-affiliate-be

# Follow logs
docker-compose logs -f shopee-affiliate-be

# Logs với timestamp
docker-compose logs -t shopee-affiliate-be
```

## 🔍 Verify Setup

Sau khi start, kiểm tra:

1. **Backend running:**
   ```bash
   docker ps | grep shopee-affiliate-be
   ```

2. **Database created:**
   ```bash
   docker exec postgres psql -U postgres -l | grep shopee_affiliate
   ```

3. **Tables created:**
   ```bash
   docker exec postgres psql -U postgres -d shopee_affiliate -c "\dt"
   ```

4. **API accessible:**
   ```bash
   curl -I http://localhost:8080/api/v1/vouchers
   ```

5. **Swagger UI:**
   Open http://localhost:8080/api/swagger-ui.html

## 🚀 Production Tips

1. **Environment variables:** Sử dụng `.env` file cho sensitive data
2. **Memory tuning:** Điều chỉnh JVM options trong docker-compose.yml
3. **Logging:** Configure log levels và log rotation
4. **Monitoring:** Add Prometheus metrics endpoint
5. **Security:** Enable authentication và HTTPS