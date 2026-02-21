#!/bin/bash

# 数据库初始化脚本（Linux/Mac）

echo "正在初始化洗车券系统数据库..."

# 数据库连接信息
DB_USER="root"
DB_PASSWORD="123456"
DB_NAME="car_wash_coupon"

# 检查MySQL是否安装
if ! command -v mysql &> /dev/null; then
    echo "错误：未找到MySQL命令，请先安装MySQL"
    exit 1
fi

# 创建数据库
echo "创建数据库..."
mysql -u "$DB_USER" -p"$DB_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行数据库初始化脚本
echo "执行数据库初始化脚本..."
mysql -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < car-wash-coupon-backend/src/main/resources/schema.sql

if [ $? -eq 0 ]; then
    echo "数据库初始化成功！"
else
    echo "数据库初始化失败，请检查脚本和数据库连接"
    exit 1
fi
