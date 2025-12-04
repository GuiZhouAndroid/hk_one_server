package com.zhangsong.hk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangsong.hk.dto.UserRegisterDTO;
import com.zhangsong.hk.dto.UserUpdateDTO;
import com.zhangsong.hk.entity.User;

public interface IUserService extends IService<User> {

    /**
     * 用户注册
     * @param dto 注册信息
     * @return 注册成功的用户信息
     */
    User register(UserRegisterDTO dto);

    /**
     * 更新用户信息
     * @param dto 更新信息
     * @return 更新后的用户信息
     */
    User updateUser(UserUpdateDTO dto);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);
}
