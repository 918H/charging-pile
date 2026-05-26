-- 评价表
CREATE TABLE IF NOT EXISTS charging_review (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    order_id BIGINT NOT NULL COMMENT '订单 ID',
    pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
    rating TINYINT NOT NULL COMMENT '评分 1-5',
    content TEXT COMMENT '评价内容',
    images VARCHAR(2000) COMMENT '图片 URL 逗号分隔',
    has_images BOOLEAN DEFAULT FALSE COMMENT '是否有图片',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_pile_id (pile_id),
    UNIQUE KEY uk_user_order (user_id, order_id)
) COMMENT='充电评价表';

-- 故障表
CREATE TABLE IF NOT EXISTS charging_fault (
    fault_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '故障 ID',
    pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
    slot_id INT COMMENT '槽位 ID',
    user_id BIGINT COMMENT '上报用户 ID',
    fault_type VARCHAR(50) NOT NULL COMMENT '故障类型',
    description TEXT NOT NULL COMMENT '故障描述',
    images VARCHAR(2000) COMMENT '图片 URL 逗号分隔',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0 待处理 1 已处理',
    handler_response TEXT COMMENT '处理回复',
    handler_id BIGINT COMMENT '处理人 ID',
    handled_at DATETIME COMMENT '处理时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_pile_id (pile_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) COMMENT='充电故障表';

-- 预约表
CREATE TABLE IF NOT EXISTS charging_reservation (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
    slot_id INT NOT NULL COMMENT '槽位 ID',
    reservation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    duration_minutes INT NOT NULL COMMENT '预约时长 (分钟)',
    estimated_fee DECIMAL(10,2) COMMENT '预估费用',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0 待使用 1 已取消 2 已完成',
    cancel_reason VARCHAR(200) COMMENT '取消原因',
    cancelled_at DATETIME COMMENT '取消时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_pile_id (pile_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
) COMMENT='充电预约表';

-- 充电会话表
CREATE TABLE IF NOT EXISTS charging_session (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话 ID',
    order_id BIGINT NOT NULL COMMENT '订单 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
    slot_id INT NOT NULL COMMENT '槽位 ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    start_soc INT COMMENT '起始电量%',
    end_soc INT COMMENT '结束电量%',
    current_soc INT COMMENT '当前电量%',
    target_soc INT COMMENT '目标电量%',
    power_consumed DECIMAL(10,2) COMMENT '充电量 kWh',
    current_voltage DECIMAL(10,2) COMMENT '当前电压 V',
    current_current DECIMAL(10,2) COMMENT '当前电流 A',
    current_power DECIMAL(10,2) COMMENT '当前功率 kW',
    avg_power DECIMAL(10,2) COMMENT '平均功率 kW',
    battery_temp DECIMAL(5,2) COMMENT '电池温度°C',
    charging_duration INT COMMENT '充电时长 (分钟)',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0 充电中 1 已完成',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_pile_id (pile_id)
) COMMENT='充电会话表';
