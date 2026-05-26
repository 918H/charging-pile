-- 新增优惠券相关表
USE charging_db;

-- 优惠券表
CREATE TABLE IF NOT EXISTS coupon (
  coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券 ID',
  coupon_name VARCHAR(128) NOT NULL COMMENT '优惠券名称',
  coupon_type TINYINT NOT NULL DEFAULT 1 COMMENT '类型 (1 满减券，2 折扣券，3 无门槛)',
  discount_amount DECIMAL(10, 2) COMMENT '优惠金额/折扣率',
  min_purchase_amount DECIMAL(10, 2) COMMENT '最低消费金额',
  max_discount_amount DECIMAL(10, 2) COMMENT '最大优惠金额',
  total_count INT NOT NULL DEFAULT 1000 COMMENT '发放总量',
  issued_count INT DEFAULT 0 COMMENT '已发放数量',
  valid_days INT COMMENT '有效天数',
  start_time DATETIME COMMENT '生效时间',
  end_time DATETIME COMMENT '过期时间',
  status TINYINT DEFAULT 1 COMMENT '状态 (1 可用，0 停用)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_status (status),
  INDEX idx_time (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券表';

-- 用户优惠券表
CREATE TABLE IF NOT EXISTS user_coupon (
  user_coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户优惠券 ID',
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  coupon_id BIGINT NOT NULL COMMENT '优惠券 ID',
  coupon_code VARCHAR(64) UNIQUE NOT NULL COMMENT '优惠券码',
  status TINYINT DEFAULT 0 COMMENT '状态 (0 未使用，1 已使用，2 已过期)',
  order_id BIGINT COMMENT '使用的订单 ID',
  used_time DATETIME COMMENT '使用时间',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  INDEX idx_coupon_code (coupon_code),
  FOREIGN KEY (coupon_id) REFERENCES coupon(coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券表';

-- 初始化优惠券数据
INSERT INTO coupon (coupon_name, coupon_type, discount_amount, min_purchase_amount, max_discount_amount, total_count, valid_days, start_time, end_time, status) VALUES
('新用户专享券', 1, 10.00, 20.00, 10.00, 1000, 30, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('满减券', 1, 5.00, 50.00, 5.00, 500, 7, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 1),
('8 折优惠券', 2, 0.8, 30.00, 20.00, 200, 15, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), 1),
('无门槛券', 3, 3.00, 0, 3.00, 300, 3, NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 1);
