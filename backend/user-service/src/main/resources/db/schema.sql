-- 充电桩小程序数据库初始化脚本
-- 版本：1.0.0
-- 创建日期：2024-05-26

CREATE DATABASE IF NOT EXISTS charging_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE charging_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
  username VARCHAR(64) UNIQUE NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码 (加密)',
  real_name VARCHAR(64) COMMENT '真实姓名',
  phone VARCHAR(20) UNIQUE COMMENT '手机号',
  email VARCHAR(128) COMMENT '邮箱',
  avatar_url VARCHAR(255) COMMENT '头像 URL',
  status TINYINT DEFAULT 1 COMMENT '状态 (1 正常，0 禁用)',
  login_time TIMESTAMP NULL COMMENT '最后登录时间',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted_at TIMESTAMP NULL COMMENT '软删除时间',
  INDEX idx_username (username),
  INDEX idx_phone (phone),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
  role_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色 ID',
  role_name VARCHAR(64) UNIQUE NOT NULL COMMENT '角色名',
  role_code VARCHAR(64) UNIQUE NOT NULL COMMENT '角色代码',
  description VARCHAR(500) COMMENT '描述',
  status TINYINT DEFAULT 1 COMMENT '状态',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
  permission_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限 ID',
  permission_name VARCHAR(64) NOT NULL COMMENT '权限名',
  permission_code VARCHAR(128) UNIQUE NOT NULL COMMENT '权限代码',
  description VARCHAR(500) COMMENT '描述',
  status TINYINT DEFAULT 1 COMMENT '状态',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  role_id BIGINT NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
  FOREIGN KEY (role_id) REFERENCES sys_role(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
  role_id BIGINT NOT NULL COMMENT '角色 ID',
  permission_id BIGINT NOT NULL COMMENT '权限 ID',
  PRIMARY KEY (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES sys_role(role_id),
  FOREIGN KEY (permission_id) REFERENCES sys_permission(permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 充电桩表
CREATE TABLE IF NOT EXISTS charging_pile (
  pile_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '充电桩 ID',
  pile_name VARCHAR(128) NOT NULL COMMENT '充电桩名称',
  pile_number VARCHAR(64) UNIQUE NOT NULL COMMENT '充电桩编号',
  location_name VARCHAR(255) COMMENT '位置名称',
  latitude DECIMAL(10, 8) COMMENT '纬度',
  longitude DECIMAL(11, 8) COMMENT '经度',
  address VARCHAR(255) COMMENT '详细地址',
  total_slots INT DEFAULT 1 COMMENT '充电口总数',
  power_type ENUM('AC', 'DC', 'AC_DC') COMMENT '电源类型',
  voltage INT COMMENT '电压 (V)',
  current_capacity INT COMMENT '电流容量 (A)',
  power_rating INT COMMENT '功率 (kW)',
  operator_id BIGINT COMMENT '运营商 ID',
  status TINYINT DEFAULT 1 COMMENT '状态 (1 正常，0 禁用)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_location (latitude, longitude),
  INDEX idx_pile_number (pile_number),
  INDEX idx_status (status),
  FOREIGN KEY (operator_id) REFERENCES sys_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电桩表';

-- 充电口表
CREATE TABLE IF NOT EXISTS charging_slot (
  slot_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '充电口 ID',
  pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
  slot_number INT COMMENT '充电口编号',
  socket_type VARCHAR(32) COMMENT '接头类型 (CCS/CHAdeMO/GB)',
  status TINYINT DEFAULT 0 COMMENT '状态 (0 空闲，1 充电中，2 维修)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (pile_id) REFERENCES charging_pile(pile_id),
  UNIQUE KEY unique_slot (pile_id, slot_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电口表';

-- 订单表
CREATE TABLE IF NOT EXISTS charging_order (
  order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单 ID',
  order_number VARCHAR(64) UNIQUE NOT NULL COMMENT '订单号',
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  pile_id BIGINT NOT NULL COMMENT '充电桩 ID',
  slot_id BIGINT COMMENT '充电口 ID',
  status TINYINT DEFAULT 0 COMMENT '状态 (0 待支付，1 充电中，2 已完成，3 已取消)',
  reserve_start DATETIME COMMENT '预约开始时间',
  reserve_end DATETIME COMMENT '预约结束时间',
  actual_start DATETIME COMMENT '实际开始时间',
  actual_end DATETIME COMMENT '实际结束时间',
  duration_minutes INT COMMENT '充电时长 (分钟)',
  power_consumed DECIMAL(10, 2) COMMENT '充电度数 (kWh)',
  unit_price DECIMAL(10, 4) COMMENT '单价 (元/度)',
  total_amount DECIMAL(10, 2) COMMENT '总金额',
  payment_status TINYINT COMMENT '支付状态 (0 未支付，1 已支付)',
  remark VARCHAR(500) COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id (user_id),
  INDEX idx_pile_id (pile_id),
  INDEX idx_status (status),
  INDEX idx_created_at (created_at),
  FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
  FOREIGN KEY (pile_id) REFERENCES charging_pile(pile_id),
  FOREIGN KEY (slot_id) REFERENCES charging_slot(slot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS payment_record (
  payment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付 ID',
  payment_number VARCHAR(64) UNIQUE NOT NULL COMMENT '支付单号',
  order_id BIGINT NOT NULL COMMENT '订单 ID',
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  amount DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
  payment_method ENUM('WECHAT', 'ALIPAY', 'CARD') COMMENT '支付方式',
  third_party_id VARCHAR(128) COMMENT '第三方交易 ID',
  status TINYINT DEFAULT 0 COMMENT '状态 (0 待支付，1 已支付，2 支付失败)',
  paid_at TIMESTAMP NULL COMMENT '支付时间',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_order_id (order_id),
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  FOREIGN KEY (order_id) REFERENCES charging_order(order_id),
  FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 初始化默认数据
INSERT INTO sys_user (username, password, real_name, phone, email, status) VALUES
('admin', '$2a$10$7JEhKvVqNzY5fU6gD8kT7.vXqN5zJ8H9X2L4K6M8N0P1Q3R5S7T9U', '超级管理员', '13800138000', 'admin@charging.com', 1),
('user', '$2a$10$7JEhKvVqNzY5fU6gD8kT7.vXqN5zJ8H9X2L4K6M8N0P1Q3R5S7T9U', '普通用户', '13900139000', 'user@charging.com', 1);

INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1),
('平台管理员', 'ADMIN', '平台管理权限', 1),
('运营人员', 'OPERATOR', '业务运营权限', 1),
('普通用户', 'USER', '基础用户权限', 1);

INSERT INTO sys_permission (permission_name, permission_code, description, status) VALUES
('查看充电桩', 'charging:view', '查看充电桩列表和详情', 1),
('管理充电桩', 'charging:manage', '管理充电桩信息', 1),
('创建订单', 'order:create', '创建充电订单', 1),
('查看订单', 'order:view', '查看订单列表和详情', 1),
('支付管理', 'payment:manage', '支付相关操作', 1),
('用户管理', 'user:manage', '用户管理权限', 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 4);

-- 初始化测试充电桩数据
INSERT INTO charging_pile (pile_name, pile_number, location_name, latitude, longitude, address, total_slots, power_type, voltage, current_capacity, power_rating, status) VALUES
('科技园充电站', 'PILE001', '深圳市南山区科技园', 22.5428, 114.0543, '广东省深圳市南山区科技南路 1 号', 10, 'DC', 380, 100, 60, 1),
('南山海岸城充电站', 'PILE002', '深圳市南山区海岸城', 22.5153, 113.9356, '广东省深圳市南山区海岸城停车场', 8, 'AC', 220, 32, 7, 1),
('福田 CBD 充电站', 'PILE003', '深圳市福田区 CBD', 22.5369, 114.0587, '广东省深圳市福田区福华三路', 12, 'AC_DC', 380, 125, 120, 1);
