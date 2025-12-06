package com.zhangsong.hk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangsong.hk.dto.UserRegisterDTO;
import com.zhangsong.hk.dto.UserUpdateDTO;
import com.zhangsong.hk.entity.User;
import com.zhangsong.hk.mapper.UserMapper;
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

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordUtil passwordUtil;

    /**
     * 用户注册 - 优化为单次查询
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(UserRegisterDTO dto) {
        // 1. 单次查询验证用户名和手机号
        validateUserNotExists(dto.getUsername(), dto.getPhone());

        // 2. 创建用户对象（直接在Service中创建）
        User nowUser = createUserFromRegisterDTO(dto);

        // 3. 保存到数据库
        boolean saveResult = this.save(nowUser);

        // 4.注册成功后分配默认角色， (0：超级管理员，1：顾客，2：配送员，3：管理员)
        int userId = nowUser.getId(); // 准备当前注册成功的用户ID
        int roleId = 1; // 默认顾客
        // todo 待实现注册时默认分配用户的角色
        return saveResult ? nowUser : null;


    }

    /**
     * 用户登录
     */
    @Override
    public User login(String username, String password) {
        // todo 待核查
        // 1. 查询用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);

        User user = this.getOne(wrapper);
        if (user == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_FOUND.getCode(), "用户不存在");
        }

        // 2. 检查账号状态
        if (user.getStatus() != 1) {
            throw new CustomException(ResultCodeEnum.FORBIDDEN.getCode(), "账号已被禁用");
        }

        // 3. 验证密码
        if (!passwordUtil.matches(password, user.getPassword())) {
            throw new CustomException(ResultCodeEnum.UNAUTHORIZED.getCode(), "密码错误");
        }

        // 4. 使用 SaToken 登录
        StpUtil.login(user.getId());

        // 5. 更新登录时间
        user.setUpdatedTime(LocalDateTime.now());
        this.updateById(user);

        return user;
    }

    /**
     * 修改密码
     */
    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        // todo 待核查
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

        // 5. 更新密码
        user.setPassword(passwordUtil.encode(newPassword));
        user.setUpdatedTime(LocalDateTime.now());

        // 6. 保存并强制重新登录
        this.updateById(user);
        StpUtil.logout();

        return true;
    }

    /**
     * 重置密码（管理员功能）
     */
    @Override
    public String resetPassword(Integer userId) {
        // todo 待核查
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
        this.updateById(user);
        StpUtil.logout(userId);

        return defaultPassword;
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(UserUpdateDTO dto) {
        // todo 待核查
        // 1. 检查用户是否存在
        User user = this.getById(dto.getId());
        if (user == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_FOUND);
        }

        // 2. 如果更新手机号，验证唯一性（排除自己）
        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(user.getPhone())) {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", dto.getPhone());
            if (this.count(wrapper) > 0) {
                throw new CustomException(ResultCodeEnum.USER_PHONE_EXISTS);
            }
        }

        // 3. 更新用户信息（直接在Service中处理）
        updateUserFromDTO(user, dto);

        // 4. 保存
        this.updateById(user);
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
     * 单次查询验证用户名和手机号是否存在
     * 如果存在，抛出具体的异常
     */
    private void validateUserNotExists(String username, String phone) {
        // 使用 OR 条件一次查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
                .or()
                .eq("phone", phone);

        List<User> existingUsers = this.list(wrapper);

        // 遍历结果，找出具体哪个已存在
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
     * 创建用户对象的私有方法 - 不对外暴露，只在Service内部使用
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
     * 退出登录
     */
    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
    }

    /**
     * 检查管理员权限（私有方法）
     */
    private void checkAdminPermission() {
        Object userType = StpUtil.getSession().get("userType");
        if (userType == null || !userType.equals((byte) 3)) {
            throw new CustomException(ResultCodeEnum.FORBIDDEN.getCode(), "需要管理员权限");
        }
    }
}
