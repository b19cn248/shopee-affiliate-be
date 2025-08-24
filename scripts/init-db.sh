#!/bin/bash

# Script để tạo database shopee_affiliate trong PostgreSQL container đã có

echo "Creating database shopee_affiliate if not exists..."

# Tạo database nếu chưa tồn tại
docker exec -i postgres psql -U postgres <<-EOSQL
    SELECT 'CREATE DATABASE shopee_affiliate'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'shopee_affiliate')\gexec
EOSQL

echo "Database shopee_affiliate is ready!"

# Kiểm tra kết nối
docker exec postgres psql -U postgres -d shopee_affiliate -c "SELECT version();"