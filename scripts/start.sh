#!/bin/bash

# Script để start Backend với PostgreSQL đã có sẵn

echo "======================================"
echo "Starting Shopee Affiliate Backend"
echo "======================================"

# 1. Kiểm tra PostgreSQL container đang chạy
echo "1. Checking PostgreSQL container..."
if [ ! "$(docker ps -q -f name=postgres)" ]; then
    echo "❌ PostgreSQL container is not running!"
    echo "Please start PostgreSQL first with:"
    echo "  docker-compose -f /path/to/postgres/docker-compose.yml up -d"
    exit 1
fi
echo "✅ PostgreSQL container is running"

# 2. Tạo database nếu chưa có
echo "2. Creating database if not exists..."
./scripts/init-db.sh

# 3. Build và start Backend
echo "3. Building and starting Backend..."
docker-compose up -d --build

# 4. Hiển thị logs
echo "4. Showing logs (press Ctrl+C to exit)..."
docker-compose logs -f