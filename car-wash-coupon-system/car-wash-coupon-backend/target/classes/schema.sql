-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `openid` VARCHAR(100) COMMENT '微信OpenID',
    `unionid` VARCHAR(100) COMMENT '微信UnionID',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `agreement_accepted` TINYINT DEFAULT 0 COMMENT '是否同意用户协议：0-否，1-是',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商家账号表
CREATE TABLE IF NOT EXISTS `merchant_account` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商家账号ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `name` VARCHAR(50) COMMENT '姓名',
    `store_id` BIGINT NOT NULL COMMENT '所属门店ID',
    `role` VARCHAR(20) DEFAULT 'staff' COMMENT '角色：admin-管理员，staff-员工',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家账号表';

-- 后台管理员表
CREATE TABLE IF NOT EXISTS `admin_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `name` VARCHAR(50) COMMENT '姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `role` VARCHAR(20) DEFAULT 'operator' COMMENT '角色：super_admin-超级管理员，finance-财务，operator-运营',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员表';

-- 门店表
CREATE TABLE IF NOT EXISTS `store` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '门店ID',
    `name` VARCHAR(100) NOT NULL COMMENT '门店名称',
    `address` VARCHAR(255) COMMENT '地址',
    `longitude` DECIMAL(10, 7) COMMENT '经度',
    `latitude` DECIMAL(10, 7) COMMENT '纬度',
    `contact_phone` VARCHAR(50) COMMENT '联系电话',
    `business_hours` VARCHAR(100) COMMENT '营业时间',
    `photos` TEXT COMMENT '门店照片JSON数组',
    `bank_name` VARCHAR(100) COMMENT '开户银行',
    `bank_account` VARCHAR(50) COMMENT '银行账号',
    `bank_account_name` VARCHAR(50) COMMENT '开户名',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

-- 卡券类型表
CREATE TABLE IF NOT EXISTS `coupon_type` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '卡券类型ID',
    `name` VARCHAR(50) NOT NULL COMMENT '类型名称（内部）',
    `display_name` VARCHAR(50) NOT NULL COMMENT '展示名称（用户可见）',
    `car_type` VARCHAR(20) COMMENT '车型：5-小轿车，7-MPV/SUV',
    `service_type` VARCHAR(20) COMMENT '服务类型：wash-洗车，maintenance-保养',
    `min_photos` INT DEFAULT 1 COMMENT '最少上传照片数量',
    `photo_example_url` VARCHAR(255) COMMENT '照片示例H5链接',
    `description` VARCHAR(255) COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡券类型表';

-- 卡券表
CREATE TABLE IF NOT EXISTS `coupon` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '卡券ID',
    `code` VARCHAR(50) NOT NULL COMMENT '卡券编码',
    `type_id` BIGINT NOT NULL COMMENT '卡券类型ID',
    `user_id` BIGINT COMMENT '所属用户ID',
    `batch_no` VARCHAR(50) COMMENT '批次号',
    `name` VARCHAR(100) NOT NULL COMMENT '卡券名称',
    `settlement_price` DECIMAL(10, 2) COMMENT '结算价格',
    `start_time` DATETIME COMMENT '生效时间',
    `end_time` DATETIME COMMENT '过期时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-未发放，1-已发放未使用，2-已使用，3-已过期',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type_id` (`type_id`),
    KEY `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡券表';

-- 卡券适用门店关联表
CREATE TABLE IF NOT EXISTS `coupon_store` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `coupon_id` BIGINT NOT NULL COMMENT '卡券ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_coupon_store` (`coupon_id`, `store_id`),
    KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡券适用门店关联表';

-- 核销记录表
CREATE TABLE IF NOT EXISTS `verification_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '核销记录ID',
    `coupon_id` BIGINT NOT NULL COMMENT '卡券ID',
    `coupon_code` VARCHAR(50) NOT NULL COMMENT '卡券编码',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `merchant_account_id` BIGINT NOT NULL COMMENT '核销人员ID',
    `photos` TEXT COMMENT '施工照片JSON数组',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待上传图片，1-审核中，2-审核通过，3-审核失败',
    `verify_time` DATETIME COMMENT '核销时间',
    `audit_time` DATETIME COMMENT '审核时间',
    `auditor_id` BIGINT COMMENT '审核人ID',
    `audit_remark` VARCHAR(255) COMMENT '审核备注',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    KEY `idx_coupon_id` (`coupon_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_merchant_account_id` (`merchant_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核销记录表';

-- 评价表
CREATE TABLE IF NOT EXISTS `evaluation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `verification_record_id` BIGINT NOT NULL COMMENT '核销记录ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `rating` TINYINT NOT NULL COMMENT '评分：1-5',
    `content` VARCHAR(500) COMMENT '评价内容',
    `photos` TEXT COMMENT '评价照片JSON数组',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_verification_record_id` (`verification_record_id`),
    KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 短信验证码表
CREATE TABLE IF NOT EXISTS `sms_code` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `code` VARCHAR(10) NOT NULL COMMENT '验证码',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：user_login-用户登录，merchant_login-商家登录',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `used` TINYINT DEFAULT 0 COMMENT '是否已使用：0-否，1-是',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_phone_type` (`phone`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码表';

-- 活动表
CREATE TABLE IF NOT EXISTS `activity` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    `title` VARCHAR(100) NOT NULL COMMENT '活动标题',
    `image_url` VARCHAR(255) NOT NULL COMMENT '活动图片URL',
    `link_url` VARCHAR(255) COMMENT '活动链接URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 功能入口表
CREATE TABLE IF NOT EXISTS `function_entry` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '功能入口ID',
    `name` VARCHAR(50) NOT NULL COMMENT '名称',
    `icon_url` VARCHAR(255) NOT NULL COMMENT '图标URL',
    `link_url` VARCHAR(255) COMMENT '跳转链接',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功能入口表';

-- 对账单表
CREATE TABLE IF NOT EXISTS `statement` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '对账单ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `year_month` VARCHAR(7) NOT NULL COMMENT '年月，格式：2024-01',
    `total_count` INT DEFAULT 0 COMMENT '总核销数量',
    `total_amount` DECIMAL(10, 2) DEFAULT 0 COMMENT '总金额',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已打款',
    `confirm_time` DATETIME COMMENT '确认时间',
    `payment_time` DATETIME COMMENT '打款时间',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    KEY `idx_store_id` (`store_id`),
    KEY `idx_year_month` (`year_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账单表';

-- 插入默认管理员
INSERT INTO `admin_user` (`username`, `password`, `name`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 'super_admin', 1);

-- 插入默认卡券类型
INSERT INTO `coupon_type` (`name`, `display_name`, `car_type`, `service_type`, `min_photos`) VALUES
('小轿车洗车', '小轿车洗车券', '5', 'wash', 1),
('SUV/MPV洗车', 'SUV/MPV洗车券', '7', 'wash', 1),
('小轿车保养', '小轿车保养券', '5', 'maintenance', 7),
('SUV/MPV保养', 'SUV/MPV保养券', '7', 'maintenance', 7);
