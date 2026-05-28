-- 支付优惠券模块表结构

-- 支付流水
CREATE TABLE IF NOT EXISTS `payment_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_no` varchar(50) NOT NULL COMMENT '交易流水号',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `channel` varchar(20) DEFAULT 'wechat' COMMENT '支付渠道：wechat/alipay/balance',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending/success/failed/refunded',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水表';

-- 支付渠道配置
CREATE TABLE IF NOT EXISTS `payment_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `channel_name` varchar(50) NOT NULL COMMENT '渠道名称',
  `channel_code` varchar(20) NOT NULL COMMENT '渠道编码',
  `app_id` varchar(100) DEFAULT NULL COMMENT 'AppID',
  `mch_id` varchar(100) DEFAULT NULL COMMENT '商户 ID',
  `api_key` varchar(255) DEFAULT NULL COMMENT 'API 密钥',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_code` (`channel_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道配置表';

-- 优惠券模板
CREATE TABLE IF NOT EXISTS `coupon_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `type` varchar(20) DEFAULT 'discount' COMMENT '类型：discount/fixed',
  `value` decimal(10,2) NOT NULL COMMENT '面值/折扣',
  `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低使用金额',
  `max_amount` decimal(10,2) DEFAULT NULL COMMENT '最高抵扣金额',
  `total_count` int(11) DEFAULT '0' COMMENT '发放总量',
  `used_count` int(11) DEFAULT '0' COMMENT '已使用量',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime DEFAULT NULL COMMENT '有效期结束',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- 用户优惠券
CREATE TABLE IF NOT EXISTS `coupon_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券 ID',
  `status` varchar(20) DEFAULT 'unused' COMMENT '状态：unused/used/expired',
  `order_no` varchar(50) DEFAULT NULL COMMENT '使用订单号',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

SELECT 'V3__payment_coupon.sql 执行完成!' AS message;
