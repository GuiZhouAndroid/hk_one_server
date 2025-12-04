package com.zhangsong.hk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangsong.hk.dto.UserRegisterDTO;
import com.zhangsong.hk.dto.UserUpdateDTO;
import com.zhangsong.hk.entity.User;
import com.zhangsong.hk.mapper.UserMapper;
import com.zhangsong.hk.service.IUserService;
import com.zhangsong.hk.utils.exception.CustomException;
import com.zhangsong.hk.utils.response.ResultCodeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(UserRegisterDTO dto) {
        // 1. 验证用户名唯一性
        if (isUsernameExists(dto.getUsername())) {
            throw new CustomException(ResultCodeEnum.ADD_USER_ERROR.getCode(), "用户名已存在");
        }

        // 2. 验证手机号唯一性
        if (isPhoneExists(dto.getPhone())) {
            throw new CustomException(ResultCodeEnum.ADD_USER_ERROR.getCode(), "手机号已注册");
        }

        // 3. DTO转Entity
        User user = new User();
        BeanUtils.copyProperties(dto, user);

        // 4. 设置默认值
        if (user.getStatus() == null) {
            user.setStatus((byte) 1); // 默认启用
        }
        if (user.getIsDelivery() == null) {
            user.setIsDelivery((byte) 0); // 默认不是配送员
        }
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        // TODO: 实际项目中应该对密码进行加密
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 5. 保存用户
        this.save(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(UserUpdateDTO dto) {
        // 1. 检查用户是否存在
        User user = this.getById(dto.getId());
        if (user == null) {
            throw new CustomException(ResultCodeEnum.NOT_FOUND.getCode(), "用户不存在");
        }

        // 2. 如果更新手机号，验证唯一性（排除自己）
        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(user.getPhone())) {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", dto.getPhone());
            if (this.count(wrapper) > 0) {
                throw new CustomException(ResultCodeEnum.ADD_USER_ERROR.getCode(), "手机号已被其他用户使用");
            }
        }

        // 3. 更新字段
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

        // 4. 更新用户
        this.updateById(user);
        return user;
    }

    @Override
    public boolean isUsernameExists(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return this.count(wrapper) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        return this.count(wrapper) > 0;
    }
}
