-- 1. 初始化角色（只用角色名称）
INSERT INTO `role` (`role_name`, `description`, `is_system`) VALUES
('客户', '普通购物客户', 1),
('配送员', '商品配送人员', 1),
('管理员', '系统管理员', 1),
('超级管理员', '系统最高权限管理员', 1);

-- 2. 初始化权限（不变）
INSERT INTO `permission` (`permission_name`) VALUES
('创建订单'),
('查看我的订单'),
('取消订单'),
('申请退款'),
('查看商品'),
('搜索商品'),
('添加收货地址'),
('修改个人信息'),
('接单'),
('查看配送订单'),
('完成配送'),
('取消配送'),
('上报位置'),
('查看配送路线'),
('修改配送状态'),
('查看配送统计'),
('查看仪表盘'),
('管理商品'),
('添加商品'),
('修改商品'),
('删除商品'),
('管理订单'),
('处理退款'),
('管理配送员'),
('查看配送员位置'),
('管理客户'),
('查看客户订单'),
('重置用户密码'),
('查看销售报表'),
('查看库存报表'),
('系统设置');

-- 3. 初始化用户（不变）
INSERT INTO `user` (`username`, `pwd`, `real_name`, `phone`, `status`) VALUES
('customer1', '$2a$10$你的加密密码', '张三', '13800138001', 1),
('customer2', '$2a$10$你的加密密码', '李四', '13800138002', 1),
('customer3', '$2a$10$你的加密密码', '王五', '13800138003', 1),
('delivery1', '$2a$10$你的加密密码', '配送员张', '13800138101', 1),
('delivery2', '$2a$10$你的加密密码', '配送员李', '13800138102', 1),
('admin', '$2a$10$你的加密密码', '系统管理员', '13800138200', 1);

-- 4. 为用户分配角色（用角色名称）
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.`id`, r.`id`
FROM `user` u, `role` r
WHERE u.`username` LIKE 'customer%'
  AND r.`role_name` = '客户';

INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.`id`, r.`id`
FROM `user` u, `role` r
WHERE u.`username` LIKE 'delivery%'
  AND r.`role_name` = '配送员';

INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.`id`, r.`id`
FROM `user` u, `role` r
WHERE u.`username` = 'admin'
  AND r.`role_name` = '超级管理员';

-- 5. 为角色分配权限（用角色名称）
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 
    r.`id`,
    p.`id`
FROM `role` r, `permission` p
WHERE r.`role_name` = '客户'
  AND p.`permission_name` IN (
    '创建订单',
    '查看我的订单',
    '取消订单',
    '申请退款',
    '查看商品',
    '搜索商品',
    '添加收货地址',
    '修改个人信息'
  );

INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 
    r.`id`,
    p.`id`
FROM `role` r, `permission` p
WHERE r.`role_name` = '配送员'
  AND p.`permission_name` IN (
    '接单',
    '查看配送订单',
    '完成配送',
    '取消配送',
    '上报位置',
    '查看配送路线',
    '修改配送状态',
    '查看配送统计'
  );

INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 
    r.`id`,
    p.`id`
FROM `role` r, `permission` p
WHERE r.`role_name` = '管理员'
  AND p.`permission_name` IN (
    '查看仪表盘',
    '管理商品',
    '添加商品',
    '修改商品',
    '删除商品',
    '管理订单',
    '处理退款',
    '管理配送员',
    '查看配送员位置',
    '管理客户',
    '查看客户订单',
    '重置用户密码',
    '查看销售报表',
    '查看库存报表'
  );

-- 超级管理员拥有所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 
    (SELECT `id` FROM `role` WHERE `role_name` = '超级管理员'),
    `id`
FROM `permission`;