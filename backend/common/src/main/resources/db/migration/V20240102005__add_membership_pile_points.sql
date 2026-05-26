CREATE TABLE IF NOT EXISTS `membership_level` (
  `level_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level_name` varchar(50) NOT NULL COMMENT '等级名称',
  `level_code` int(11) NOT NULL COMMENT '等级代码 (0-普通 1-白银 2-黄金 3-白金 4-钻石)',
  `discount_rate` decimal(3,2) NOT NULL DEFAULT '1.00' COMMENT '折扣率',
  `upgrade_threshold` decimal(10,2) NOT NULL COMMENT '升级门槛 (累计消费金额)',
  `valid_days` int(11) NOT NULL DEFAULT '365' COMMENT '有效期天数',
  `benefits` text COMMENT '权益说明',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`level_id`),
  UNIQUE KEY `uk_level_code` (`level_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

INSERT INTO `membership_level` VALUES 
(1, '普通会员', 0, 1.00, 0, 365, '基础充电服务', 1, NOW(), NOW()),
(2, '白银会员', 1, 0.98, 500, 365, '98 折优惠 + 专属客服', 1, NOW(), NOW()),
(3, '黄金会员', 2, 0.95, 2000, 365, '95 折优惠 + 免费预约 + 专属客服', 1, NOW(), NOW()),
(4, '白金会员', 3, 0.92, 5000, 365, '92 折优惠 + 免费预约 + 优先充电 + 专属客服', 1, NOW(), NOW()),
(5, '钻石会员', 4, 0.90, 10000, 365, '90 折优惠 + 所有权益 + 生日礼包', 1, NOW(), NOW());

CREATE TABLE IF NOT EXISTS `user_membership` (
  `user_membership_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `level_id` bigint(20) NOT NULL COMMENT '会员等级 ID',
  `level_code` int(11) NOT NULL COMMENT '等级代码',
  `start_time` datetime NOT NULL COMMENT '生效时间',
  `end_time` datetime NOT NULL COMMENT '失效时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-失效 1-有效',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_membership_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status_time` (`status`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会员表';

CREATE TABLE IF NOT EXISTS `user_recharge_card` (
  `card_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `card_number` varchar(32) NOT NULL COMMENT '卡号',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `freeze_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`card_id`),
  UNIQUE KEY `uk_card_number` (`card_number`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户储值卡表';

CREATE TABLE IF NOT EXISTS `recharge_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `card_id` bigint(20) NOT NULL COMMENT '储值卡 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `bonus_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '赠送金额',
  `payment_method` tinyint(4) NOT NULL COMMENT '支付方式 1-微信 2-支付宝 3-银行卡',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '支付流水号',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0-待支付 1-已完成 2-失败',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_card_id` (`card_id`),
  KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';

CREATE TABLE IF NOT EXISTS `user_points` (
  `points_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `points` int(11) NOT NULL DEFAULT '0' COMMENT '可用积分',
  `frozen_points` int(11) NOT NULL DEFAULT '0' COMMENT '冻结积分',
  `expires_at` datetime DEFAULT NULL COMMENT '过期时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`points_id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分表';

CREATE TABLE IF NOT EXISTS `points_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `points` int(11) NOT NULL COMMENT '积分数量 (正数增加 负数减少)',
  `type` tinyint(4) NOT NULL COMMENT '类型 1-增加 2-消费',
  `description` varchar(255) NOT NULL COMMENT '说明',
  `related_order` varchar(64) DEFAULT NULL COMMENT '关联订单',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

CREATE TABLE IF NOT EXISTS `private_pile` (
  `pile_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '桩主 ID',
  `pile_name` varchar(100) NOT NULL COMMENT '充电桩名称',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '经度',
  `power_type` tinyint(4) NOT NULL COMMENT '电力类型 1-交流 7kW 2-直流 60kW 3-直流 120kW',
  `connector_type` tinyint(4) NOT NULL COMMENT '接口类型 1-国标 2-欧标 3-美标',
  `charging_speed` decimal(5,2) NOT NULL COMMENT '充电速度 kW',
  `available_time` varchar(50) DEFAULT NULL COMMENT '可用时间段',
  `price_per_kwh` decimal(5,2) NOT NULL COMMENT '电价 元/kWh',
  `service_fee` decimal(5,2) NOT NULL DEFAULT '0.50' COMMENT '服务费 元/kWh',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0-私用 1-共享 2-维护中',
  `total_energy` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总充电量 kWh',
  `total_sessions` int(11) NOT NULL DEFAULT '0' COMMENT '总充电次数',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '评分',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pile_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_location` (`latitude`,`longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私人充电桩表';

CREATE TABLE IF NOT EXISTS `pile_reservation` (
  `reservation_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pile_id` bigint(20) NOT NULL COMMENT '充电桩 ID',
  `user_id` bigint(20) NOT NULL COMMENT '预约用户 ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0-待使用 1-充电中 2-已完成 3-已取消 4-违约',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `actual_energy` decimal(10,2) DEFAULT '0.00' COMMENT '实际充电量 kWh',
  `total_fee` decimal(10,2) DEFAULT '0.00' COMMENT '总费用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`reservation_id`),
  KEY `idx_pile_id` (`pile_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_time` (`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私人充电桩预约表';

CREATE TABLE IF NOT EXISTS `pile_income` (
  `income_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pile_id` bigint(20) NOT NULL COMMENT '充电桩 ID',
  `user_id` bigint(20) NOT NULL COMMENT '桩主 ID',
  `order_id` varchar(64) NOT NULL COMMENT '订单 ID',
  `income_amount` decimal(10,2) NOT NULL COMMENT '收入金额',
  `platform_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '平台抽成',
  `actual_income` decimal(10,2) NOT NULL COMMENT '实际收入',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0-待结算 1-已结算 2-已提现',
  `settlement_time` datetime DEFAULT NULL COMMENT '结算时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`income_id`),
  KEY `idx_pile_id` (`pile_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私人充电桩收入表';

CREATE TABLE IF NOT EXISTS `sign_in_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `sign_in_date` date NOT NULL COMMENT '签到日期',
  `continuous_days` int(11) NOT NULL DEFAULT '1' COMMENT '连续天数',
  `points_awarded` int(11) NOT NULL DEFAULT '0' COMMENT '奖励积分',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`sign_in_date`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';

CREATE TABLE IF NOT EXISTS `referral_relation` (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `referrer_id` bigint(20) NOT NULL COMMENT '推荐人 ID',
  `referee_id` bigint(20) NOT NULL COMMENT '被推荐人 ID',
  `referral_code` varchar(32) NOT NULL COMMENT '推荐码',
  `reward_points` int(11) NOT NULL DEFAULT '0' COMMENT '奖励积分',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0-待激活 1-已激活 2-已奖励',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `uk_referee` (`referee_id`),
  KEY `idx_referrer` (`referrer_id`),
  KEY `idx_code` (`referral_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐关系表';
