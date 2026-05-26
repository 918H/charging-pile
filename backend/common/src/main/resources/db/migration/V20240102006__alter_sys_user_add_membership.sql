ALTER TABLE `sys_user` 
ADD COLUMN `total_spending` DECIMAL(10,2) DEFAULT '0.00' COMMENT '累计消费金额' AFTER `status`,
ADD COLUMN `membership_level` INT(11) DEFAULT '0' COMMENT '会员等级 (0-普通 1-白银 2-黄金 3-白金 4-钻石)' AFTER `total_spending`,
ADD COLUMN `points` INT(11) DEFAULT '0' COMMENT '积分' AFTER `membership_level`;
