-- 新功能模块表结构

-- 日统计数据
CREATE TABLE IF NOT EXISTS `statistics_daily` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `stat_date` date NOT NULL COMMENT '统计日期',
  `new_user_count` int(11) DEFAULT '0' COMMENT '新增用户数',
  `active_user_count` int(11) DEFAULT '0' COMMENT '活跃用户数',
  `order_count` int(11) DEFAULT '0' COMMENT '订单数',
  `order_amount` decimal(12,2) DEFAULT '0.00' COMMENT '订单金额',
  `revenue` decimal(12,2) DEFAULT '0.00' COMMENT '营收',
  `charging_count` int(11) DEFAULT '0' COMMENT '充电次数',
  `charging_duration` decimal(10,2) DEFAULT '0.00' COMMENT '充电时长 (小时)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日统计数据表';

-- 充电桩统计
CREATE TABLE IF NOT EXISTS `pile_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pile_id` bigint(20) NOT NULL COMMENT '充电桩 ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `charging_count` int(11) DEFAULT '0' COMMENT '充电次数',
  `charging_duration` decimal(10,2) DEFAULT '0.00' COMMENT '充电时长',
  `energy_total` decimal(10,2) DEFAULT '0.00' COMMENT '总充电量',
  `revenue` decimal(12,2) DEFAULT '0.00' COMMENT '营收',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_pile_date` (`pile_id`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电桩统计表';

-- 消息模板
CREATE TABLE IF NOT EXISTS `message_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL COMMENT '类型',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容模板',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

-- 用户消息
CREATE TABLE IF NOT EXISTS `user_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` varchar(50) DEFAULT 'system' COMMENT '类型',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表';

-- 客服工单
CREATE TABLE IF NOT EXISTS `support_ticket` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ticket_no` varchar(50) NOT NULL COMMENT '工单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `type` varchar(50) DEFAULT 'complaint' COMMENT '类型',
  `content` text NOT NULL COMMENT '内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态',
  `priority` varchar(20) DEFAULT 'normal' COMMENT '优先级',
  `handler_id` bigint(20) DEFAULT NULL COMMENT '处理人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ticket_no` (`ticket_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服工单表';

-- 工单回复
CREATE TABLE IF NOT EXISTS `ticket_reply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ticket_id` bigint(20) NOT NULL COMMENT '工单 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `content` text NOT NULL COMMENT '回复内容',
  `type` tinyint(1) DEFAULT '1' COMMENT '类型：1 用户 2 客服',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ticket_id` (`ticket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单回复表';

-- 营销活动
CREATE TABLE IF NOT EXISTS `marketing_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `type` varchar(20) DEFAULT 'discount' COMMENT '类型',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) DEFAULT 'draft' COMMENT '状态',
  `participant_count` int(11) DEFAULT '0' COMMENT '参与人数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动表';

-- 活动参与
CREATE TABLE IF NOT EXISTS `activity_participation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_id` bigint(20) NOT NULL COMMENT '活动 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `status` varchar(20) DEFAULT 'participated' COMMENT '状态',
  `reward` varchar(255) DEFAULT NULL COMMENT '奖励',
  `participate_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动参与表';

-- 用户签到
CREATE TABLE IF NOT EXISTS `user_sign_in` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `sign_in_date` date NOT NULL COMMENT '签到日期',
  `continuous_days` int(11) DEFAULT '1' COMMENT '连续天数',
  `points` int(11) DEFAULT '0' COMMENT '获得积分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `sign_in_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户签到表';

-- 财务流水
CREATE TABLE IF NOT EXISTS `finance_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `type` varchar(20) NOT NULL COMMENT '类型',
  `amount` decimal(12,2) NOT NULL COMMENT '金额',
  `status` varchar(20) DEFAULT 'success' COMMENT '状态',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `order_no` varchar(50) DEFAULT NULL COMMENT '关联订单',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务流水表';

-- 发票
CREATE TABLE IF NOT EXISTS `invoice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `invoice_no` varchar(50) DEFAULT NULL COMMENT '发票号码',
  `amount` decimal(12,2) NOT NULL COMMENT '金额',
  `type` varchar(20) DEFAULT 'electronic' COMMENT '类型',
  `title` varchar(200) DEFAULT NULL COMMENT '发票抬头',
  `tax_id` varchar(50) DEFAULT NULL COMMENT '税号',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票表';

-- 充电桩实时状态
CREATE TABLE IF NOT EXISTS `pile_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pile_id` bigint(20) NOT NULL COMMENT '充电桩 ID',
  `status` varchar(20) DEFAULT 'idle' COMMENT '状态',
  `power` decimal(5,2) DEFAULT '0.00' COMMENT '功率',
  `voltage` decimal(5,2) DEFAULT '220.00' COMMENT '电压',
  `current` decimal(5,2) DEFAULT '0.00' COMMENT '电流',
  `temperature` decimal(5,2) DEFAULT '25.00' COMMENT '温度',
  `soc` int(11) DEFAULT '0' COMMENT '电量',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_pile_id` (`pile_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电桩实时状态表';

-- 告警记录
CREATE TABLE IF NOT EXISTS `pile_alarm` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pile_id` bigint(20) NOT NULL COMMENT '充电桩 ID',
  `type` varchar(50) NOT NULL COMMENT '告警类型',
  `level` varchar(20) DEFAULT 'warning' COMMENT '级别',
  `content` varchar(500) NOT NULL COMMENT '内容',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态',
  `resolve_result` varchar(500) DEFAULT NULL COMMENT '处理结果',
  `alarm_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `resolve_time` datetime DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_pile_id` (`pile_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

SELECT 'V4__new_features.sql 执行完成!' AS message;
