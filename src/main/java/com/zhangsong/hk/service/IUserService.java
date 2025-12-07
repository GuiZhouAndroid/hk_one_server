package com.zhangsong.hk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangsong.hk.dto.UserRegisterDTO;
import com.zhangsong.hk.dto.UserUpdateDTO;
import com.zhangsong.hk.entity.User;

/**
 * @ClassName: IUserService
 * @Description: 用户服务接口
 * @Author: 张松
 * @Date: 2025/12/8 20:12
 * @Version: 1.0
 */
public interface IUserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param dto 用户注册DTO
     * @return 注册成功的用户信息
     */
    User register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息
     */
    User login(String username, String password);

    /**
     * 更新用户信息
     *
     * @param dto 用户更新DTO
     * @return 更新后的用户信息
     */
    User updateUser(UserUpdateDTO dto);

    /**
     * 修改密码
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(String oldPassword, String newPassword);

    /**
     * 重置密码（管理员功能）
     *
     * @param userId 用户ID
     * @return 新密码
     */
    String resetPassword(Integer userId);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 获取在线用户总数
     *
     * @return 在线用户数量
     */
    int getOnlineCount();

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);
}