-- 用户模块表结构

-- 用户表
CREATE TABLE IF NOT EXISTS `user_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码 (加密)',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像 URL',
  `role` varchar(20) DEFAULT 'user' COMMENT '角色：admin/user',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `points` int(11) DEFAULT '0' COMMENT '积分',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户档案
CREATE TABLE IF NOT EXISTS `user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别：0 未知 1 男 2 女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户档案表';

-- 用户余额变动
CREATE TABLE IF NOT EXISTS `user_balance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `type` varchar(20) NOT NULL COMMENT '类型：recharge/payment/refund',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `balance_before` decimal(10,2) NOT NULL COMMENT '变动前余额',
  `balance_after` decimal(10,2) NOT NULL COMMENT '变动后余额',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `order_no` varchar(50) DEFAULT NULL COMMENT '关联订单号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户余额变动表';

-- 登录日志
CREATE TABLE IF NOT EXISTS `user_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP 地址',
  `device` varchar(100) DEFAULT NULL COMMENT '设备信息',
  `status` varchar(20) DEFAULT 'success' COMMENT '状态：success/failed',
  `reason` varchar(255) DEFAULT NULL COMMENT '失败原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

SELECT 'V1__user.sql 执行完成!' AS message;
