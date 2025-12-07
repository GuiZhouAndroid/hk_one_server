package com.zhangsong.hk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangsong.hk.dto.UserRegisterDTO;
import com.zhangsong.hk.dto.UserUpdateDTO;
import com.zhangsong.hk.entity.Role;
import com.zhangsong.hk.entity.User;
import com.zhangsong.hk.mapper.UserMapper;
import com.zhangsong.hk.service.IRoleService;
import com.zhangsong.hk.service.IUserService;
import com.zhangsong.hk.utils.PasswordUtil;
import com.zhangsong.hk.utils.exception.CustomException;
import com.zhangsong.hk.utils.response.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: UserServiceImpl
 * @Description: 用户服务实现类，包含用户管理、角色分配和SaToken登录功能
 * @Author: 张松
 * @Date: 2025/12/8 20:12
 * @Version: 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private IRoleService roleService;

    /**
     * 用户注册 - 包含角色自动分配
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(UserRegisterDTO dto) {
        // 1. 单次查询验证用户名和手机号
        validateUserNotExists(dto.getUsername(), dto.getPhone());

        // 2. 创建用户对象
        User nowUser = createUserFromRegisterDTO(dto);

        // 3. 保存到数据库
        this.save(nowUser);

        // 4. 根据用户类型分配默认角色
        assignDefaultRole(nowUser.getId(), dto.getUserType());

        return nowUser;
    }

    /**
     * 用户登录 - 集成SaToken登录和角色信息存储
     */
    @Override
    public User login(String username, String password) {
        // 1. 查询用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);

        User user = this.getOne(wrapper);
        if (user == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_FOUND);
        }

        // 2. 检查账号状态
        if (user.getStatus() != 1) {
            throw new CustomException(ResultCodeEnum.FORBIDDEN.getCode(), "账号已被禁用");
        }

        // 3. 验证密码
        if (!passwordUtil.matches(password, user.getPassword())) {
            throw new CustomException(ResultCodeEnum.UNAUTHORIZED.getCode(), "密码错误");
        }

        // 4. 查询用户角色
        List<Role> roles = roleService.getRolesByUserId(user.getId());
        List<String> roleCodes = roles.stream()
                .map(Role::getRoleCode)
                .toList();

        // 5. 使用 SaToken 登录，并存储用户信息和角色
        StpUtil.login(user.getId());

        // 存储用户信息到Session
        StpUtil.getSession().set("user", user);
        StpUtil.getSession().set("userType", user.getUserType());
        StpUtil.getSession().set("roles", roleCodes);

        // 为当前会话添加角色标识
        for (String roleCode : roleCodes) {
            StpUtil.getRoleList().add(roleCode);
        }

        // 6. 更新登录时间
        user.setUpdatedTime(LocalDateTime.now());
        this.updateById(user);

        return user;
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(String oldPassword, String newPassword) {
        // 1. 获取当前用户ID
        Object userId = StpUtil.getLoginId();
        if (userId == null) {
            throw new CustomException(ResultCodeEnum.UNAUTHORIZED);
        }

        // 2. 查询用户
        User user = this.getById((Integer) userId);
        if (user == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_FOUND);
        }

        // 3. 验证原密码
        if (!passwordUtil.matches(oldPassword, user.getPassword())) {
            throw new CustomException(ResultCodeEnum.UNAUTHORIZED.getCode(), "原密码错误");
        }

        // 4. 密码强度验证
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new CustomException(ResultCodeEnum.BAD_REQUEST.getCode(), "密码长度需为6-20位");
        }

        // 5. 更新密码
        user.setPassword(passwordUtil.encode(newPassword));
        user.setUpdatedTime(LocalDateTime.now());

        // 6. 保存并强制重新登录
        boolean updateResult = this.updateById(user);
        if (updateResult) {
            StpUtil.logout();
        }

        return updateResult;
    }

    /**
     * 重置密码（管理员功能）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetPassword(Integer userId) {
        // 1. 检查管理员权限
        checkAdminPermission();

        // 2. 查询用户
        User user = this.getById(userId);
        if (user == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_FOUND);
        }

        // 3. 生成默认密码
        String defaultPassword = passwordUtil.generateDefaultPassword();

        // 4. 更新密码
        user.setPassword(passwordUtil.encode(defaultPassword));
        user.setUpdatedTime(LocalDateTime.now());

        // 5. 保存并强制用户下线
        boolean updateResult = this.updateById(user);
        if (updateResult) {
            StpUtil.logout(userId);
        } else {
            throw new CustomException(ResultCodeEnum.FAIL.getCode(), "重置密码失败");
        }

        return defaultPassword;
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(UserUpdateDTO dto) {
        // 1. 检查用户是否存在
        User user = this.getById(dto.getId());
        if (user == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_FOUND);
        }

        // 2. 如果更新手机号，验证唯一性（排除自己）
        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(user.getPhone())) {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", dto.getPhone())
                    .ne("id", dto.getId());
            if (this.count(wrapper) > 0) {
                throw new CustomException(ResultCodeEnum.USER_PHONE_EXISTS);
            }
        }

        // 3. 更新用户信息
        updateUserFromDTO(user, dto);

        // 4. 保存
        boolean updateResult = this.updateById(user);
        if (!updateResult) {
            throw new CustomException(ResultCodeEnum.UPDATE_USER_ERROR);
        }

        // 5. 如果用户是当前登录用户，更新Session
        Object currentUserId = StpUtil.getLoginId();
        if (currentUserId != null && currentUserId.equals(user.getId())) {
            StpUtil.getSession().set("user", user);
        }

        return user;
    }

    /**
     * 获取在线用户总数
     */
    @Override
    public int getOnlineCount() {
        return 0;
    }

    /**
     * 检查用户名是否存在
     */
    @Override
    public boolean isUsernameExists(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return this.count(wrapper) > 0;
    }

    /**
     * 检查手机号是否存在
     */
    @Override
    public boolean isPhoneExists(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        return this.count(wrapper) > 0;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
    }

    /**
     * 单次查询验证用户名和手机号是否存在
     * @param username 用户名
     * @param phone 手机号
     */
    private void validateUserNotExists(String username, String phone) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
                .or()
                .eq("phone", phone);

        List<User> existingUsers = this.list(wrapper);

        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(username)) {
                throw new CustomException(ResultCodeEnum.USER_EXISTS);
            }
            if (existingUser.getPhone().equals(phone)) {
                throw new CustomException(ResultCodeEnum.USER_PHONE_EXISTS);
            }
        }
    }

    /**
     * 根据用户类型分配默认角色
     * @param userId 用户ID
     * @param userType 用户类型：1-客户 2-员工 3-管理员
     */
    private void assignDefaultRole(Integer userId, Byte userType) {
        String roleCode;

        switch (userType) {
            case 1: // 客户
                roleCode = "CUSTOMER";
                break;
            case 2: // 员工
                roleCode = "STAFF";
                break;
            case 3: // 管理员
                roleCode = "ADMIN";
                break;
            default:
                roleCode = "CUSTOMER";
        }

        Role role = roleService.getRoleByCode(roleCode);
        if (role != null) {
            roleService.assignRoleToUser(userId, role.getId());
        }
    }

    /**
     * 创建用户对象的私有方法
     * @param dto 用户注册DTO
     * @return 用户对象
     */
    private User createUserFromRegisterDTO(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordUtil.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setUserType(dto.getUserType());
        user.setGender(dto.getGender() != null ? dto.getGender() : (byte) 0);
        user.setIsDelivery(dto.getIsDelivery() != null ? dto.getIsDelivery() : (byte) 0);
        user.setStatus((byte) 1);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        return user;
    }

    /**
     * 更新用户信息的私有方法
     * @param user 用户对象
     * @param dto 用户更新DTO
     */
    private void updateUserFromDTO(User user, UserUpdateDTO dto) {
        if (StringUtils.hasText(dto.getRealName())) {
            user.setRealName(dto.getRealName());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getIsDelivery() != null) {
            user.setIsDelivery(dto.getIsDelivery());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        user.setUpdatedTime(LocalDateTime.now());
    }

    /**
     * 检查管理员权限
     */
    private void checkAdminPermission() {
        if (!StpUtil.hasRole("ADMIN")) {
            throw new CustomException(ResultCodeEnum.FORBIDDEN.getCode(), "需要管理员权限");
        }
    }
}