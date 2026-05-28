-- 创建数据库
CREATE DATABASE IF NOT EXISTS charging_pile DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE charging_pile;

-- 说明：具体的表结构在 V1__user.sql, V2__charging_order.sql, V3__payment_coupon.sql, V4__new_features.sql 中定义

-- 初始化基础数据
INSERT INTO message_template (type, title, content, status) VALUES 
('ORDER_STATUS', '订单状态通知', '您的订单 {orderNo} 状态已更新为 {status}', 'active'),
('PAYMENT_SUCCESS', '支付成功通知', '您已成功支付 {amount} 元', 'active'),
('RECHARGE_SUCCESS', '充值成功通知', '您已充值 {amount} 元，当前余额 {balance} 元', 'active'),
('REFUND_PROGRESS', '退款进度通知', '您的退款 {refundNo} 正在处理中', 'active'),
('ACTIVITY_PROMOTION', '活动推广', '新活动 {activityName} 已上线，快来参与吧！', 'active');

-- 初始化默认管理员 (密码需要加密，这里仅示例)
-- INSERT INTO user_user (username, password, phone, role, status) VALUES ('admin', 'admin123', '13800138000', 'admin', 'active');

SELECT '数据库初始化完成!' AS message;
