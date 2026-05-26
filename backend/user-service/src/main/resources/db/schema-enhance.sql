-- 充电枪表增强
USE charging_db;

-- 修改充电口表，添加更多字段
ALTER TABLE charging_slot ADD COLUMN socket_type VARCHAR(32) COMMENT '接头类型 (GB/T 新国标，CCS,CHAdeMO,Type2)';
ALTER TABLE charging_slot ADD COLUMN socket_power INT COMMENT '充电功率 (kW)';
ALTER TABLE charging_slot ADD COLUMN current_status TINYINT DEFAULT 0 COMMENT '当前状态 (0 空闲，1 占用，2 充电中，3 故障，4 维护)';
ALTER TABLE charging_slot ADD COLUMN locked TINYINT DEFAULT 0 COMMENT '是否锁定 (1 锁定，0 未锁定)';
ALTER TABLE charging_slot ADD COLUMN current_order_id BIGINT COMMENT '当前订单 ID';
ALTER TABLE charging_slot ADD COLUMN last_maintenance_time DATETIME COMMENT '上次维护时间';

-- 充电记录表（详细充电数据）
CREATE TABLE IF NOT EXISTS charging_session (
  session_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '充电会话 ID',
  order_id BIGINT NOT NULL UNIQUE COMMENT '订单 ID',
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
  slot_id BIGINT NOT NULL COMMENT '充电口 ID',
  start_time DATETIME COMMENT '开始充电时间',
  end_time DATETIME COMMENT '结束充电时间',
  start_soc INT COMMENT '开始 SOC(%)',
  end_soc INT COMMENT '结束 SOC(%)',
  start_voltage DECIMAL(10,2) COMMENT '开始电压 (V)',
  end_voltage DECIMAL(10,2) COMMENT '结束电压 (V)',
  start_current DECIMAL(10,2) COMMENT '开始电流 (A)',
  end_current DECIMAL(10,2) COMMENT '结束电流 (A)',
  max_power DECIMAL(10,2) COMMENT '最大功率 (kW)',
  avg_power DECIMAL(10,2) COMMENT '平均功率 (kW)',
  power_consumed DECIMAL(10,2) COMMENT '充电度数 (kWh)',
  charging_duration INT COMMENT '充电时长 (分钟)',
  charging_efficiency DECIMAL(5,4) COMMENT '充电效率',
  battery_temp DECIMAL(5,2) COMMENT '电池温度 (℃)',
  status TINYINT DEFAULT 0 COMMENT '状态 (0 充电中，1 已完成，2 异常终止)',
  stop_reason VARCHAR(255) COMMENT '停止原因',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_order_id (order_id),
  INDEX idx_user_id (user_id),
  INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电会话记录表';

-- 充电价格表（峰谷平电价）
CREATE TABLE IF NOT EXISTS charging_price (
  price_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '价格 ID',
  pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
  price_type TINYINT NOT NULL DEFAULT 1 COMMENT '电价类型 (1 峰时，2 平时，3 谷时)',
  start_time TIME NOT NULL COMMENT '开始时间',
  end_time TIME NOT NULL COMMENT '结束时间',
  electricity_price DECIMAL(10,4) NOT NULL COMMENT '电费单价 (元/kWh)',
  service_price DECIMAL(10,4) NOT NULL DEFAULT 0 COMMENT '服务费单价 (元/kWh)',
  total_price DECIMAL(10,4) NOT NULL COMMENT '合计单价 (元/kWh)',
  effective_date DATE COMMENT '生效日期',
  expire_date DATE COMMENT '失效日期',
  status TINYINT DEFAULT 1 COMMENT '状态 (1 生效，0 停用)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_pile_id (pile_id),
  INDEX idx_type (price_type),
  INDEX idx_time (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电价格表';

-- 初始化峰谷电价
INSERT INTO charging_price (pile_id, price_type, start_time, end_time, electricity_price, service_price, total_price) VALUES
(1, 1, '08:00:00', '12:00:00', 1.2000, 0.5000, 1.7000),  -- 峰时
(1, 2, '12:00:00', '14:00:00', 0.8000, 0.5000, 1.3000),  -- 平时
(1, 1, '14:00:00', '22:00:00', 1.2000, 0.5000, 1.7000),  -- 峰时
(1, 3, '22:00:00', '23:59:59', 0.4000, 0.5000, 0.9000),  -- 谷时
(1, 3, '00:00:00', '08:00:00', 0.4000, 0.5000, 0.9000);  -- 谷时

-- 预约充电表
CREATE TABLE IF NOT EXISTS charging_reservation (
  reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约 ID',
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
  slot_id BIGINT NOT NULL COMMENT '充电口 ID',
  reservation_time DATETIME NOT NULL COMMENT '预约时间',
  expire_time DATETIME NOT NULL COMMENT '过期时间',
  status TINYINT DEFAULT 0 COMMENT '状态 (0 待履约，1 履约成功，2 已取消，3 已过期)',
  order_id BIGINT COMMENT '关联订单 ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_id (user_id),
  INDEX idx_pile_id (pile_id),
  INDEX idx_time (reservation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约充电表';

-- 占位费规则表
CREATE TABLE IF NOT EXISTS occupation_fee_rule (
  rule_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则 ID',
  pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
  free_duration INT DEFAULT 30 COMMENT '免费时长 (分钟)',
  fee_per_minute DECIMAL(10,2) NOT NULL DEFAULT 0.5 COMMENT '每分钟收费 (元)',
  max_fee DECIMAL(10,2) NOT NULL DEFAULT 100 COMMENT '最高收费 (元)',
  enabled TINYINT DEFAULT 1 COMMENT '是否启用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_pile_id (pile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='占位费规则表';

-- 初始化占位费规则
INSERT INTO occupation_fee_rule (pile_id, free_duration, fee_per_minute, max_fee) VALUES
(1, 30, 0.50, 100),
(2, 30, 0.50, 100),
(3, 30, 0.50, 100);

-- 故障上报表
CREATE TABLE IF NOT EXISTS pile_fault (
  fault_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '故障 ID',
  pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
  slot_id BIGINT COMMENT '充电口 ID',
  user_id BIGINT COMMENT '上报用户 ID',
  fault_type VARCHAR(64) NOT NULL COMMENT '故障类型',
  fault_description VARCHAR(500) COMMENT '故障描述',
  fault_image VARCHAR(500) COMMENT '故障图片',
  status TINYINT DEFAULT 0 COMMENT '状态 (0 待处理，1 处理中，2 已解决，3 误报)',
  handler_id BIGINT COMMENT '处理人 ID',
  handle_result VARCHAR(500) COMMENT '处理结果',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  resolved_at DATETIME COMMENT '解决时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_pile_id (pile_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故障上报表';

-- 用户评价表
CREATE TABLE IF NOT EXISTS pile_review (
  review_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价 ID',
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
  order_id BIGINT NOT NULL UNIQUE COMMENT '订单 ID',
  rating TINYINT NOT NULL COMMENT '评分 (1-5 星)',
  environment_rating TINYINT DEFAULT 0 COMMENT '环境评分 (1-5 星)',
  facility_rating TINYINT DEFAULT 0 COMMENT '设施评分 (1-5 星)',
  content VARCHAR(500) COMMENT '评价内容',
  images VARCHAR(1000) COMMENT '评价图片',
  reply_content VARCHAR(500) COMMENT '回复内容',
  reply_time DATETIME COMMENT '回复时间',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_pile_id (pile_id),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户评价表';
