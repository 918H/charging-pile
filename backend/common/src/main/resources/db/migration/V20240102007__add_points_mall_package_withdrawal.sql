CREATE TABLE IF NOT EXISTS `points_mall_item` (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) NOT NULL COMMENT '商品名称',
  `item_desc` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `item_image` varchar(255) DEFAULT NULL COMMENT '商品图片 URL',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型 1-优惠券 2-充电券 3-实物 4-虚拟商品',
  `points_price` int(11) NOT NULL COMMENT '积分价格',
  `cash_value` decimal(10,2) DEFAULT '0.00' COMMENT '现金价值',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存数量',
  `limit_per_user` int(11) NOT NULL DEFAULT '1' COMMENT '每人限兑数量',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `coupon_config` text COMMENT '优惠券配置 JSON',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商城商品表';

INSERT INTO `points_mall_item` VALUES 
(1, '5 元充电优惠券', '满 20 元可用', NULL, 1, 500, 5.00, 1000, 5, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, '{"type":"discount","amount":5,"minAmount":20}', NOW(), NOW()),
(2, '10 元充电优惠券', '满 50 元可用', NULL, 1, 1000, 10.00, 500, 3, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, '{"type":"discount","amount":10,"minAmount":50}', NOW(), NOW()),
(3, '20 元充电优惠券', '满 100 元可用', NULL, 1, 2000, 20.00, 200, 2, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, '{"type":"discount","amount":20,"minAmount":100}', NOW(), NOW()),
(4, '5 元无门槛券', '无使用门槛', NULL, 1, 800, 5.00, 100, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, '{"type":"cash","amount":5}', NOW(), NOW()),
(5, '100 积分', '直接充值 100 积分', NULL, 2, 100, 1.00, 9999, 10, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), 1, '{"type":"points","amount":100}', NOW(), NOW()),
(6, '定制充电宝', '10000mAh 快充版', NULL, 3, 5000, 50.00, 50, 1, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), 1, NULL, NOW(), NOW()),
(7, '车载充电器', '双 USB 快充', NULL, 3, 3000, 30.00, 100, 2, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), 1, NULL, NOW(), NOW()),
(8, '充电服务费 9 折券', '服务费 9 折优惠', NULL, 1, 1500, 5.00, 300, 3, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, '{"type":"service_discount","rate":0.9}', NOW(), NOW());

CREATE TABLE IF NOT EXISTS `points_exchange_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `item_id` bigint(20) NOT NULL COMMENT '商品 ID',
  `item_name` varchar(100) NOT NULL COMMENT '商品名称',
  `points_used` int(11) NOT NULL COMMENT '消耗积分',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0-待发货 1-已发货 2-已完成 3-已取消',
  `shipping_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `tracking_number` varchar(64) DEFAULT NULL COMMENT '快递单号',
  `exchange_time` datetime NOT NULL COMMENT '兑换时间',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分兑换记录表';

CREATE TABLE IF NOT EXISTS `charging_package` (
  `package_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `package_name` varchar(100) NOT NULL COMMENT '套餐名称',
  `package_desc` varchar(500) DEFAULT NULL COMMENT '套餐描述',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型 1-电量包 2-时段包 3-周期包',
  `price` decimal(10,2) NOT NULL COMMENT '套餐价格',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `included_energy` decimal(10,2) NOT NULL COMMENT '包含电量 kWh',
  `valid_days` int(11) NOT NULL COMMENT '有效期天数',
  `time_limit_start` int(11) DEFAULT NULL COMMENT '时段限制开始 (小时)',
  `time_limit_end` int(11) DEFAULT NULL COMMENT '时段限制结束 (小时)',
  `purchase_limit` int(11) NOT NULL DEFAULT '1' COMMENT '每人限购数量',
  `sold_count` int(11) NOT NULL DEFAULT '0' COMMENT '已售数量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `start_time` datetime NOT NULL COMMENT '开售时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`package_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电套餐表';

INSERT INTO `charging_package` VALUES 
(1, '月卡 100 元', '包含 120kWh 电量，30 天内有效', 1, 100.00, 150.00, 120.00, 30, NULL, NULL, 2, 0, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), NOW(), NOW()),
(2, '季卡 280 元', '包含 360kWh 电量，90 天内有效', 1, 280.00, 450.00, 360.00, 90, NULL, NULL, 1, 0, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), NOW(), NOW()),
(3, '夜间充电包', '22:00-次日 8:00 可用，100kWh', 2, 80.00, 120.00, 100.00, 30, 22, 8, 3, 0, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), NOW(), NOW()),
(4, '周末充电包', '周六日可用，50kWh', 2, 50.00, 75.00, 50.00, 30, NULL, NULL, 5, 0, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), NOW(), NOW()),
(5, '半年卡 500 元', '包含 650kWh 电量，180 天内有效', 1, 500.00, 900.00, 650.00, 180, NULL, NULL, 1, 0, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), NOW(), NOW());

CREATE TABLE IF NOT EXISTS `user_package` (
  `user_package_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `package_id` bigint(20) NOT NULL COMMENT '套餐 ID',
  `remaining_energy` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '剩余电量 kWh',
  `start_time` datetime NOT NULL COMMENT '生效时间',
  `end_time` datetime NOT NULL COMMENT '失效时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-已用完 1-有效 2-已过期',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_package_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户套餐表';

CREATE TABLE IF NOT EXISTS `pile_withdrawal` (
  `withdrawal_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `amount` decimal(10,2) NOT NULL COMMENT '提现金额',
  `fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '手续费',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实际到账金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0-待审核 1-已通过 2-已拒绝 3-已打款',
  `alipay_account` varchar(100) DEFAULT NULL COMMENT '支付宝账号',
  `alipay_name` varchar(50) DEFAULT NULL COMMENT '支付宝实名',
  `bank_account` varchar(100) DEFAULT NULL COMMENT '银行账号',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户行',
  `bank_card` varchar(32) DEFAULT NULL COMMENT '银行卡号',
  `withdraw_method` tinyint(4) NOT NULL COMMENT '提现方式 1-支付宝 2-银行卡',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '交易流水号',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`withdrawal_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私桩提现记录表';
