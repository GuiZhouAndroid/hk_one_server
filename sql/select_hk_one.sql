-- ==================== 基础查询 ====================

-- 1. 查询所有用户
SELECT * FROM `user`;

-- 2. 查询所有角色
SELECT * FROM `role`;

-- 3. 查询所有权限
SELECT * FROM `permission`;

-- 4. 查询用户角色关系
SELECT * FROM `user_role`;

-- 5. 查询角色权限关系
SELECT * FROM `role_permission`;

-- ==================== 常用业务查询 ====================

-- 1. 查询用户及其角色（详细）
SELECT 
    u.`id` as 用户ID,
    u.`username` as 用户名,
    u.`real_name` as 真实姓名,
    u.`phone` as 手机号,
    u.`status` as 状态,
    GROUP_CONCAT(r.`role_name`) as 角色列表
FROM `user` u
LEFT JOIN `user_role` ur ON u.`id` = ur.`user_id`
LEFT JOIN `role` r ON ur.`role_id` = r.`id`
GROUP BY u.`id`
ORDER BY u.`id`;

-- 2. 查询角色及其权限（详细）
SELECT 
    r.`id` as 角色ID,
    r.`role_name` as 角色名称,
    r.`description` as 描述,
    GROUP_CONCAT(p.`permission_name`) as 权限列表
FROM `role` r
LEFT JOIN `role_permission` rp ON r.`id` = rp.`role_id`
LEFT JOIN `permission` p ON rp.`permission_id` = p.`id`
GROUP BY r.`id`
ORDER BY r.`id`;

-- 3. 查询单个用户的权限（登录时用）
SELECT 
    p.`permission_name` as 权限名称
FROM `user` u
LEFT JOIN `user_role` ur ON u.`id` = ur.`user_id`
LEFT JOIN `role_permission` rp ON ur.`role_id` = rp.`role_id`
LEFT JOIN `permission` p ON rp.`permission_id` = p.`id`
WHERE u.`username` = 'customer1'  -- 根据用户名查询
ORDER BY p.`permission_name`;

-- 4. 查询单个用户的角色（登录时用）
SELECT 
    r.`role_name` as 角色名称
FROM `user` u
LEFT JOIN `user_role` ur ON u.`id` = ur.`user_id`
LEFT JOIN `role` r ON ur.`role_id` = r.`id`
WHERE u.`username` = 'customer1'
ORDER BY r.`role_name`;

-- 5. 查询配送员用户
SELECT 
    u.`id`,
    u.`username`,
    u.`real_name`,
    u.`phone`,
    u.`status`,
    u.`last_login_time`
FROM `user` u
WHERE EXISTS (
    SELECT 1 FROM `user_role` ur
    JOIN `role` r ON ur.`role_id` = r.`id`
    WHERE ur.`user_id` = u.`id`
    AND r.`role_name` = '配送员'
)
ORDER BY u.`id`;

-- 6. 查询管理员用户
SELECT 
    u.`id`,
    u.`username`,
    u.`real_name`,
    u.`phone`,
    u.`status`
FROM `user` u
WHERE EXISTS (
    SELECT 1 FROM `user_role` ur
    JOIN `role` r ON ur.`role_id` = r.`id`
    WHERE ur.`user_id` = u.`id`
    AND r.`role_name` IN ('管理员', '超级管理员')
)
ORDER BY u.`id`;

-- 7. 查询客户用户
SELECT 
    u.`id`,
    u.`username`,
    u.`real_name`,
    u.`phone`,
    u.`status`,
    u.`created_time`
FROM `user` u
WHERE EXISTS (
    SELECT 1 FROM `user_role` ur
    JOIN `role` r ON ur.`role_id` = r.`id`
    WHERE ur.`user_id` = u.`id`
    AND r.`role_name` = '客户'
)
ORDER BY u.`created_time` DESC;

-- ==================== 权限检查相关查询 ====================

-- 8. 检查用户是否有某个权限
SELECT 
    CASE WHEN COUNT(*) > 0 THEN '有权限' ELSE '无权限' END as 权限检查结果
FROM `user` u
LEFT JOIN `user_role` ur ON u.`id` = ur.`user_id`
LEFT JOIN `role_permission` rp ON ur.`role_id` = rp.`role_id`
LEFT JOIN `permission` p ON rp.`permission_id` = p.`id`
WHERE u.`id` = 1  -- 用户ID
  AND p.`permission_name` = '创建订单';  -- 要检查的权限

-- 9. 检查用户是否有某个角色
SELECT 
    CASE WHEN COUNT(*) > 0 THEN '是' ELSE '否' END as 是否拥有角色
FROM `user` u
LEFT JOIN `user_role` ur ON u.`id` = ur.`user_id`
LEFT JOIN `role` r ON ur.`role_id` = r.`id`
WHERE u.`id` = 1
  AND r.`role_name` = '客户';

-- 10. 查询用户的所有角色和权限（综合视图）
SELECT 
    u.`username`,
    u.`real_name`,
    r.`role_name`,
    p.`permission_name`
FROM `user` u
JOIN `user_role` ur ON u.`id` = ur.`user_id`
JOIN `role` r ON ur.`role_id` = r.`id`
JOIN `role_permission` rp ON r.`id` = rp.`role_id`
JOIN `permission` p ON rp.`permission_id` = p.`id`
ORDER BY u.`username`, r.`role_name`, p.`permission_name`;

-- ==================== 统计查询 ====================

-- 11. 统计各角色用户数量
SELECT 
    r.`role_name` as 角色,
    COUNT(DISTINCT u.`id`) as 用户数量
FROM `role` r
LEFT JOIN `user_role` ur ON r.`id` = ur.`role_id`
LEFT JOIN `user` u ON ur.`user_id` = u.`id`
GROUP BY r.`id`
ORDER BY 用户数量 DESC;

-- 12. 统计权限使用情况（哪些角色有该权限）
SELECT 
    p.`permission_name` as 权限,
    GROUP_CONCAT(r.`role_name`) as 拥有的角色
FROM `permission` p
LEFT JOIN `role_permission` rp ON p.`id` = rp.`permission_id`
LEFT JOIN `role` r ON rp.`role_id` = r.`id`
GROUP BY p.`id`
ORDER BY p.`permission_name`;

-- 13. 查询最近登录的用户
SELECT 
    u.`username`,
    u.`real_name`,
    u.`last_login_time`,
    u.`login_count`,
    GROUP_CONCAT(r.`role_name`) as 角色
FROM `user` u
LEFT JOIN `user_role` ur ON u.`id` = ur.`user_id`
LEFT JOIN `role` r ON ur.`role_id` = r.`id`
WHERE u.`last_login_time` IS NOT NULL
GROUP BY u.`id`
ORDER BY u.`last_login_time` DESC
LIMIT 10;

-- ==================== 管理后台常用查询 ====================

-- 14. 查询需要管理的用户（分页查询）
SELECT 
    u.`id`,
    u.`username`,
    u.`real_name`,
    u.`phone`,
    u.`status`,
    u.`last_login_time`,
    u.`created_time`,
    GROUP_CONCAT(r.`role_name`) as 角色
FROM `user` u
LEFT JOIN `user_role` ur ON u.`id` = ur.`user_id`
LEFT JOIN `role` r ON ur.`role_id` = r.`id`
GROUP BY u.`id`
ORDER BY u.`created_time` DESC
LIMIT 0, 20;  -- 分页：从第0条开始，取20条

-- 15. 查询角色详情及权限（用于编辑角色）
SELECT 
    r.`id`,
    r.`role_name`,
    r.`description`,
    r.`is_system`,
    r.`status`,
    GROUP_CONCAT(p.`id`) as 权限ID列表,
    GROUP_CONCAT(p.`permission_name`) as 权限名称列表
FROM `role` r
LEFT JOIN `role_permission` rp ON r.`id` = rp.`role_id`
LEFT JOIN `permission` p ON rp.`permission_id` = p.`id`
WHERE r.`id` = 1  -- 角色ID
GROUP BY r.`id`;

-- 16. 搜索用户（按用户名、手机号、姓名）
SELECT 
    u.`id`,
    u.`username`,
    u.`real_name`,
    u.`phone`,
    u.`status`,
    GROUP_CONCAT(r.`role_name`) as 角色
FROM `user` u
LEFT JOIN `user_role` ur ON u.`id` = ur.`user_id`
LEFT JOIN `role` r ON ur.`role_id` = r.`id`
WHERE u.`username` LIKE '%customer%'  -- 搜索条件
   OR u.`real_name` LIKE '%张%'
   OR u.`phone` LIKE '%138%'
GROUP BY u.`id`
ORDER BY u.`id`;

-- ==================== 权限管理相关查询 ====================

-- 17. 查询未分配给角色的权限
SELECT 
    p.`id`,
    p.`permission_name`,
    p.`created_time`
FROM `permission` p
WHERE NOT EXISTS (
    SELECT 1 FROM `role_permission` rp
    WHERE rp.`permission_id` = p.`id`
)
ORDER BY p.`permission_name`;

-- 18. 查询未分配权限的角色
SELECT 
    r.`id`,
    r.`role_name`,
    r.`description`
FROM `role` r
WHERE NOT EXISTS (
    SELECT 1 FROM `role_permission` rp
    WHERE rp.`role_id` = r.`id`
)
ORDER BY r.`role_name`;

-- 19. 查询用户未拥有的权限（用于分配权限）
SELECT 
    p.`id`,
    p.`permission_name`
FROM `permission` p
WHERE NOT EXISTS (
    SELECT 1 
    FROM `user_role` ur
    JOIN `role_permission` rp ON ur.`role_id` = rp.`role_id`
    WHERE ur.`user_id` = 1  -- 指定用户ID
    AND rp.`permission_id` = p.`id`
)
ORDER BY p.`permission_name`;

-- ==================== 系统维护查询 ====================

-- 20. 检查数据一致性（查找无效的外键引用）
-- 查找user_role表中引用不存在的用户
SELECT ur.* 
FROM `user_role` ur
LEFT JOIN `user` u ON ur.`user_id` = u.`id`
WHERE u.`id` IS NULL;

-- 查找user_role表中引用不存在的角色
SELECT ur.* 
FROM `user_role` ur
LEFT JOIN `role` r ON ur.`role_id` = r.`id`
WHERE r.`id` IS NULL;

-- 查找role_permission表中引用不存在的角色
SELECT rp.* 
FROM `role_permission` rp
LEFT JOIN `role` r ON rp.`role_id` = r.`id`
WHERE r.`id` IS NULL;

-- 查找role_permission表中引用不存在的权限
SELECT rp.* 
FROM `role_permission` rp
LEFT JOIN `permission` p ON rp.`permission_id` = p.`id`
WHERE p.`id` IS NULL;

-- 21. 备份查询（导出数据）
SELECT * FROM `user` INTO OUTFILE '/tmp/user_backup.csv'
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n';

SELECT * FROM `role` INTO OUTFILE '/tmp/role_backup.csv'
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n';

SELECT * FROM `permission` INTO OUTFILE '/tmp/permission_backup.csv'
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n';