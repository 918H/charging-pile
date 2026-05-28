-- ============================================
-- 新功能模块数据库迁移脚本
-- 版本：4.0
-- 日期：2026-05-28
-- 内容：数据分析、消息通知、客服工单、营销、财务、设备监控
-- ============================================

-- 1. 数据分析模块
-- ============================================

-- 1.1 日统计数据表
CREATE TABLE IF NOT EXISTS `statistics_daily` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `total_orders` INT DEFAULT 0 COMMENT '总订单数',
  `total_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '总金额',
  `total_kwh` DECIMAL(10,2) DEFAULT 0 COMMENT '总充电量 (kWh)',
  `active_users` INT DEFAULT 0 COMMENT '活跃用户数',
  `new_users` INT DEFAULT 0 COMMENT '新增用户数',
  `avg_charging_time` INT DEFAULT 0 COMMENT '平均充电时长 (分钟)',
  `peak_orders` INT DEFAULT 0 COMMENT '峰时订单数',
  `off_peak_orders` INT DEFAULT 0 COMMENT '谷时订单数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date` (`stat_date`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日统计报表';

-- 1.2 充电桩使用统计
CREATE TABLE IF NOT EXISTS `pile_statistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pile_id` BIGINT NOT NULL COMMENT '充电桩 ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `usage_count` INT DEFAULT 0 COMMENT '使用次数',
  `usage_hours` DECIMAL(10,2) DEFAULT 0 COMMENT '使用时长 (小时)',
  `revenue` DECIMAL(10,2) DEFAULT 0 COMMENT '收益',
  `avg_power` DECIMAL(10,2) DEFAULT 0 COMMENT '平均功率 (kW)',
  `total_kwh` DECIMAL(10,2) DEFAULT 0 COMMENT '总充电量',
  `error_count` INT DEFAULT 0 COMMENT '故障次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pile_date` (`pile_id`, `stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电桩统计报表';

-- 2. 消息通知模块
-- ============================================

-- 2.1 消息模板表
CREATE TABLE IF NOT EXISTS `message_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` VARCHAR(50) NOT NULL COMMENT '消息类型',
  `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `params` VARCHAR(500) COMMENT '参数列表',
  `channel` VARCHAR(50) DEFAULT '站内信' COMMENT '发送渠道',
  `enabled` TINYINT DEFAULT 1 COMMENT '是否启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

-- 2.2 用户消息表
CREATE TABLE IF NOT EXISTS `user_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `type` VARCHAR(50) NOT NULL COMMENT '消息类型',
  `channel` VARCHAR(50) DEFAULT '站内信' COMMENT '发送渠道',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读',
  `read_time` DATETIME COMMENT '阅读时间',
  `template_id` BIGINT COMMENT '模板 ID',
  `biz_id` VARCHAR(100) COMMENT '业务 ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表';

-- 3. 客服工单模块
-- ============================================

-- 3.1 工单主表
CREATE TABLE IF NOT EXISTS `support_ticket` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ticket_no` VARCHAR(50) NOT NULL COMMENT '工单号',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `type` VARCHAR(50) NOT NULL COMMENT '工单类型',
  `priority` VARCHAR(20) DEFAULT 'normal' COMMENT '优先级',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  `subject` VARCHAR(200) NOT NULL COMMENT '主题',
  `content` TEXT NOT NULL COMMENT '内容',
  `attachments` VARCHAR(1000) COMMENT '附件',
  `assigned_to` BIGINT COMMENT '处理人 ID',
  `created_by` BIGINT NOT NULL COMMENT '创建人',
  `resolved_by` BIGINT COMMENT '解决人',
  `resolved_time` DATETIME COMMENT '解决时间',
  `satisfaction_score` TINYINT COMMENT '满意度',
  `satisfaction_comment` TEXT COMMENT '满意度评价',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ticket_no` (`ticket_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服工单表';

-- 3.2 工单回复表
CREATE TABLE IF NOT EXISTS `ticket_reply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ticket_id` BIGINT NOT NULL COMMENT '工单 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `user_type` VARCHAR(20) NOT NULL COMMENT '用户类型',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `attachments` VARCHAR(1000) COMMENT '附件',
  `is_internal` TINYINT DEFAULT 0 COMMENT '是否内部备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ticket_id` (`ticket_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单回复表';

-- 4. 营销活动模块
-- ============================================

-- 4.1 营销活动表
CREATE TABLE IF NOT EXISTS `marketing_activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(100) NOT NULL COMMENT '活动名称',
  `type` VARCHAR(50) NOT NULL COMMENT '活动类型',
  `description` TEXT COMMENT '活动描述',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `budget` DECIMAL(10,2) DEFAULT 0 COMMENT '预算',
  `used_budget` DECIMAL(10,2) DEFAULT 0 COMMENT '已用预算',
  `rule_json` TEXT COMMENT '规则 JSON',
  `participation_limit` INT COMMENT '参与次数限制',
  `total_participants` INT DEFAULT 0 COMMENT '参与人数',
  `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动表';

-- 4.2 活动参与记录
CREATE TABLE IF NOT EXISTS `activity_participation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `reward_type` VARCHAR(50) COMMENT '奖励类型',
  `reward_amount` DECIMAL(10,2) COMMENT '奖励金额',
  `reward_points` INT COMMENT '奖励积分',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_user` (`activity_id`, `user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动参与记录';

-- 4.3 签到记录表
CREATE TABLE IF NOT EXISTS `user_sign_in` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `sign_date` DATE NOT NULL COMMENT '签到日期',
  `continuous_days` INT DEFAULT 1 COMMENT '连续签到天数',
  `reward_points` INT DEFAULT 0 COMMENT '奖励积分',
  `reward_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '奖励金额',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `sign_date`),
  KEY `idx_sign_date` (`sign_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户签到表';

-- 5. 财务报表模块
-- ============================================

-- 5.1 财务流水表
CREATE TABLE IF NOT EXISTS `finance_transaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `transaction_no` VARCHAR(50) NOT NULL COMMENT '流水号',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `type` VARCHAR(50) NOT NULL COMMENT '交易类型',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `balance_before` DECIMAL(10,2) COMMENT '交易前余额',
  `balance_after` DECIMAL(10,2) COMMENT '交易后余额',
  `related_order_id` BIGINT COMMENT '关联订单 ID',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务流水表';

-- 5.2 发票表
CREATE TABLE IF NOT EXISTS `invoice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `invoice_no` VARCHAR(50) COMMENT '发票号码',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '发票抬头',
  `tax_id` VARCHAR(100) COMMENT '税号',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '开票金额',
  `type` VARCHAR(50) DEFAULT '电子普通发票' COMMENT '发票类型',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  `invoice_url` VARCHAR(500) COMMENT '发票 PDF 地址',
  `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `issue_time` DATETIME COMMENT '开票时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invoice_no` (`invoice_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票表';

-- 6. 设备监控模块
-- ============================================

-- 6.1 充电桩状态表
CREATE TABLE IF NOT EXISTS `pile_status` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pile_id` BIGINT NOT NULL COMMENT '充电桩 ID',
  `status` VARCHAR(20) NOT NULL COMMENT '状态',
  `power` DECIMAL(10,2) DEFAULT 0 COMMENT '当前功率 (kW)',
  `voltage` DECIMAL(10,2) DEFAULT 0 COMMENT '电压 (V)',
  `current` DECIMAL(10,2) DEFAULT 0 COMMENT '电流 (A)',
  `temperature` DECIMAL(5,2) DEFAULT 0 COMMENT '温度 (℃)',
  `error_code` VARCHAR(50) COMMENT '错误码',
  `last_heartbeat` DATETIME COMMENT '最后心跳时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pile_id` (`pile_id`),
  KEY `idx_status` (`status`),
  KEY `idx_last_heartbeat` (`last_heartbeat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电桩状态表';

-- 6.2 告警记录表
CREATE TABLE IF NOT EXISTS `pile_alarm` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pile_id` BIGINT NOT NULL COMMENT '充电桩 ID',
  `alarm_type` VARCHAR(50) NOT NULL COMMENT '告警类型',
  `alarm_level` VARCHAR(20) DEFAULT 'warning' COMMENT '告警级别',
  `content` TEXT NOT NULL COMMENT '告警内容',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态',
  `handler_id` BIGINT COMMENT '处理人 ID',
  `handle_result` TEXT COMMENT '处理结果',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `resolve_time` DATETIME COMMENT '解决时间',
  PRIMARY KEY (`id`),
  KEY `idx_pile_id` (`pile_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电桩告警表';

-- 7. 初始化数据
-- ============================================

-- 7.1 消息模板初始化
INSERT INTO `message_template` (`type`, `name`, `title`, `content`, `params`, `channel`, `enabled`) VALUES
('ORDER_STATUS', '订单状态通知', '订单状态变更', '您的订单 {orderNo} 状态已变更为 {status}', 'orderNo,status', '站内信', 1),
('PAYMENT_SUCCESS', '支付成功通知', '支付成功', '您的订单 {orderNo} 已支付成功，金额{amount}元', 'orderNo,amount', '站内信', 1),
('RECHARGE_SUCCESS', '充值成功通知', '充值成功', '您已成功充值{amount}元，当前余额{balance}元', 'amount,balance', '站内信', 1),
('REFUND_PROGRESS', '退款进度通知', '退款进度', '您的退款申请{refundNo}已{action}', 'refundNo,action', '站内信', 1),
('ACTIVITY_PROMOTION', '活动推广', '新活动上线', '{activityName}活动已上线，快来参加！', 'activityName', '站内信', 1);

