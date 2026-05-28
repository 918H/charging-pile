-- 充电订单模块表结构

-- 充电桩表
CREATE TABLE IF NOT EXISTS `charging_pile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pile_no` varchar(50) NOT NULL COMMENT '充电桩编号',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `type` varchar(20) DEFAULT 'ac' COMMENT '类型：ac/dc',
  `power` decimal(5,2) DEFAULT '7.00' COMMENT '功率 (kW)',
  `status` varchar(20) DEFAULT 'idle' COMMENT '状态：idle/charging/fault',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '经度',
  `price_peak` decimal(10,2) DEFAULT '2.00' COMMENT '峰时电价',
  `price_flat` decimal(10,2) DEFAULT '1.50' COMMENT '平时电价',
  `price_valley` decimal(10,2) DEFAULT '0.80' COMMENT '谷时电价',
  `service_fee` decimal(10,2) DEFAULT '0.50' COMMENT '服务费',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pile_no` (`pile_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电桩表';

-- 充电订单
CREATE TABLE IF NOT EXISTS `charging_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `pile_id` bigint(20) NOT NULL COMMENT '充电桩 ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` int(11) DEFAULT '0' COMMENT '充电时长 (分钟)',
  `energy` decimal(10,2) DEFAULT '0.00' COMMENT '充电量 (kWh)',
  `amount` decimal(10,2) DEFAULT '0.00' COMMENT '订单金额',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending/charging/completed/cancelled',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电订单表';

-- 充电记录
CREATE TABLE IF NOT EXISTS `charging_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单 ID',
  `power` decimal(5,2) DEFAULT '0.00' COMMENT '实时功率',
  `voltage` decimal(5,2) DEFAULT '220.00' COMMENT '电压',
  `current` decimal(5,2) DEFAULT '0.00' COMMENT '电流',
  `temperature` decimal(5,2) DEFAULT '25.00' COMMENT '温度',
  `soc` int(11) DEFAULT '0' COMMENT '电量百分比',
  `record_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电记录表';

-- 订单费用明细
CREATE TABLE IF NOT EXISTS `order_fee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单 ID',
  `energy_fee` decimal(10,2) DEFAULT '0.00' COMMENT '电费',
  `service_fee` decimal(10,2) DEFAULT '0.00' COMMENT '服务费',
  `parking_fee` decimal(10,2) DEFAULT '0.00' COMMENT '占位费',
  `coupon_fee` decimal(10,2) DEFAULT '0.00' COMMENT '优惠券抵扣',
  `total_fee` decimal(10,2) DEFAULT '0.00' COMMENT '总费用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单费用明细表';

-- 订单退款
CREATE TABLE IF NOT EXISTS `order_refund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单 ID',
  `refund_no` varchar(50) NOT NULL COMMENT '退款单号',
  `amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending/processing/success/failed',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款表';

SELECT 'V2__charging_order.sql 执行完成!' AS message;
