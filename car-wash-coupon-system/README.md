# 洗车券发放及核销系统

## 项目简介

本项目是一个完整的洗车券发放及核销系统，包括用户小程序、商家小程序和后端服务三个主要部分。系统实现了洗车券的发放、管理、核销和审核等全流程功能。

## 技术栈

### 后端技术
- Spring Boot 3.2.0
- Java 17
- MyBatis-Plus
- MySQL 8.0
- Redis
- JWT
- Aliyun SMS

### 前端技术
- 微信小程序原生开发

## 项目结构

```
car-wash-coupon-system/
├── car-wash-coupon-backend/         # 后端服务
│   ├── src/                         # 源代码
│   ├── pom.xml                      # Maven配置
│   └── target/                      # 编译输出
├── car-wash-coupon-user-miniapp/    # 用户小程序
│   ├── pages/                       # 页面
│   ├── utils/                       # 工具类
│   ├── app.js                       # 应用入口
│   ├── app.json                     # 应用配置
│   └── app.wxss                     # 全局样式
├── car-wash-coupon-merchant-miniapp/# 商家小程序
│   ├── pages/                       # 页面
│   ├── utils/                       # 工具类
│   ├── app.js                       # 应用入口
│   ├── app.json                     # 应用配置
│   └── app.wxss                     # 全局样式
├── start-backend.sh                 # 后端启动脚本（Linux/Mac）
├── start-backend.bat                # 后端启动脚本（Windows）
├── init-database.sh                 # 数据库初始化脚本（Linux/Mac）
├── init-database.bat                # 数据库初始化脚本（Windows）
└── README.md                        # 项目说明
```

## 功能模块

### 用户小程序
- 登录（手机+验证码）
- 优惠券管理（查看、使用）
- 门店查询（附近门店、门店详情）
- 服务评价

### 商家小程序
- 商家登录
- 扫码核销
- 核销记录管理
- 施工照片上传

### 后端服务
- 用户认证
- 优惠券管理
- 门店管理
- 核销验证
- 评价管理
- 后台管理

## 环境搭建

### 1. 数据库配置

1. 安装MySQL 8.0
2. 创建数据库：`car_wash_coupon`
3. 执行数据库初始化脚本：
   - Linux/Mac: `./init-database.sh`
   - Windows: `init-database.bat`

### 2. 后端服务

1. 进入后端目录：`cd car-wash-coupon-backend`
2. 编译项目：`mvn clean package`
3. 启动服务：
   - Linux/Mac: `./start-backend.sh`
   - Windows: `start-backend.bat`

### 3. 小程序配置

1. 微信开发者工具中导入小程序项目
2. 修改小程序中的API基础地址（如需）
3. 编译运行小程序

## API文档

后端服务启动后，可通过以下地址访问API文档：
- [http://localhost:8080/doc.html](http://localhost:8080/doc.html)

## 注意事项

1. 本项目使用了Aliyun SMS服务，需要配置相应的AccessKey
2. 小程序需要在微信公众平台注册并获取AppID
3. 后端服务默认端口为8080
4. 数据库默认配置：用户名`root`，密码`123456`

## 联系方式

如有问题，请联系项目负责人。