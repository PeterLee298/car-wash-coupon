@echo off

REM 数据库初始化脚本（Windows）

echo 正在初始化洗车券系统数据库...

REM 数据库连接信息
set DB_USER=root
set DB_PASSWORD=lee000321
set DB_NAME=car_wash_coupon

REM 检查MySQL是否安装
mysql --version > nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到MySQL命令，请先安装MySQL并添加到环境变量
    pause
    exit /b 1
)

REM 创建数据库
echo 创建数据库...
mysql -u "%DB_USER%" -p"%DB_PASSWORD%" -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

REM 执行数据库初始化脚本
echo 执行数据库初始化脚本...
mysql -u "%DB_USER%" -p"%DB_PASSWORD%" "%DB_NAME%" < car-wash-coupon-backend\src\main\resources\schema.sql

if %errorlevel% equ 0 (
    echo 数据库初始化成功！
) else (
    echo 数据库初始化失败，请检查脚本和数据库连接
    pause
    exit /b 1
)

pause
