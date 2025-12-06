package com.zhangsong.hk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhangsong.hk.dto.UserRegisterDTO;
import com.zhangsong.hk.dto.UserUpdateDTO;
import com.zhangsong.hk.entity.User;
import com.zhangsong.hk.service.IUserService;
import com.zhangsong.hk.utils.exception.CustomException;
import com.zhangsong.hk.utils.response.R;
import com.zhangsong.hk.utils.response.ResultCodeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: UserController
 * @Description: 用户控制器
 * @Author: 张松
 * @Date: 2025/12/3 22:20
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R register(@Valid @RequestBody UserRegisterDTO dto) {
        User user = userService.register(dto);
        return R.ok(ResultCodeEnum.ADD_USER_SUCCESS.getMsg()).data(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public R updateUser(@Valid @RequestBody UserUpdateDTO dto) {
        User user = userService.updateUser(dto);
        return R.ok(ResultCodeEnum.ADD_UPDATE_SUCCESS.getMsg()).data(user);
    }

    /**
     * 根据ID删除用户（软删除）
     */
    @DeleteMapping("/delete/{id}")
    public R<Void> deleteUser(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new CustomException(ResultCodeEnum.NOT_FOUND.getCode(), "用户不存在");
        }

        // 软删除：更新状态为0
        user.setStatus((byte) 0);
        user.setUpdatedTime(LocalDateTime.now());
        boolean result = userService.updateById(user);

        if (!result) {
            throw new CustomException(ResultCodeEnum.FAIL.getCode(), "操作失败");
        }
        return R.ok("用户已禁用");
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/get/{id}")
    public R getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_FOUND.getCode(), ResultCodeEnum.USER_NOT_FOUND.getMsg());
        }
        return R.ok().data(user);
    }

    /**
     * 查询所有用户（仅查询启用状态的用户）
     */
    @GetMapping("/list")
    public R listAllUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
                .orderByDesc("created_time");

        List<User> users = userService.list(wrapper);
        return R.ok().data(users);
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public R pageUsers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("username", keyword)
                    .or()
                    .like("real_name", keyword)
                    .or()
                    .like("phone", keyword);
        }

        wrapper.eq("status", 1)
                .orderByDesc("created_time");

        IPage<User> pageResult = userService.page(page, wrapper);
        return R.ok().data(pageResult);
    }

    /**
     * 条件查询用户
     */
    @GetMapping("/query")
    public R queryUsers(
            @RequestParam(required = false) Byte userType,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) Byte gender) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (userType != null) {
            wrapper.eq("user_type", userType);
        }
        if (status != null) {
            wrapper.eq("status", status);
        } else {
            wrapper.eq("status", 1);
        }
        if (gender != null) {
            wrapper.eq("gender", gender);
        }

        wrapper.orderByDesc("created_time");

        List<User> users = userService.list(wrapper);
        return R.ok().data(users);
    }

    /**
     * 验证用户名是否可用
     * 使用JSR-303验证参数，不再手动验证
     */
    @GetMapping("/check/username/{username}")
    public R checkUsername(
            @PathVariable
            @NotBlank(message = "用户名不能为空")
            @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名只能包含字母、数字和下划线，长度3-20位")
            String username) {

        boolean exists = userService.isUsernameExists(username);
        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        result.put("available", !exists);
        result.put("suggestions", exists ? "建议使用其他用户名" : "用户名可用");

        return R.ok(exists ? "用户名已存在" : "用户名可用").data(result);
    }

    /**
     * 验证手机号是否可用
     * 使用JSR-303验证参数，不再手动验证
     */
    @GetMapping("/check/phone/{phone}")
    public R checkPhone(
            @PathVariable
            @NotBlank(message = "手机号不能为空")
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
            String phone) {

        boolean exists = userService.isPhoneExists(phone);
        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        result.put("available", !exists);

        return R.ok(exists ? "手机号已注册" : "手机号可用").data(result);
    }

    /**
     * 统计用户数量
     */
    @GetMapping("/count")
    public R countUsers(
            @RequestParam(required = false) Byte userType,
            @RequestParam(required = false) Byte status) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (userType != null) {
            wrapper.eq("user_type", userType);
        }
        if (status != null) {
            wrapper.eq("status", status);
        } else {
            wrapper.eq("status", 1);
        }

        long count = userService.count(wrapper);
        return R.ok().data(count);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R login(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
                .eq("password", password)  // 实际项目应该用加密密码比较
                .eq("status", 1);

        User user = userService.getOne(wrapper);
        if (user == null) {
            throw new CustomException(ResultCodeEnum.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }

        // 更新最后登录时间
        user.setUpdatedTime(LocalDateTime.now());
        userService.updateById(user);

        return R.ok("登录成功").data(user);
    }
}
