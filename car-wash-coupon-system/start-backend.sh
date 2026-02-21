#!/bin/bash

# 后端服务启动脚本（Linux/Mac）

echo "正在启动洗车券后端服务..."

# 检查是否存在编译后的jar包
if [ ! -f "car-wash-coupon-backend/target/car-wash-coupon-backend-0.0.1-SNAPSHOT.jar" ]; then
    echo "错误：未找到编译后的jar包，请先执行 mvn clean package 编译项目"
    exit 1
fi

# 进入后端目录
cd car-wash-coupon-backend

# 启动服务
echo "启动服务中..."
java -jar target/car-wash-coupon-backend-0.0.1-SNAPSHOT.jar
