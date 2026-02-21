@echo off

REM 后端服务启动脚本（Windows）

echo 正在启动洗车券后端服务...

REM 检查是否存在编译后的jar包
if not exist "car-wash-coupon-backend\target\car-wash-coupon-backend-0.0.1-SNAPSHOT.jar" (
    echo 错误：未找到编译后的jar包，请先执行 mvn clean package 编译项目
    pause
    exit /b 1
)

REM 进入后端目录
cd car-wash-coupon-backend

REM 启动服务
echo 启动服务中...
java -jar target\car-wash-coupon-backend-0.0.1-SNAPSHOT.jar

pause
