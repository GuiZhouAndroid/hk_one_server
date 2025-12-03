package com.zhangsong.hk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhangsong.hk.entity.User;
import com.zhangsong.hk.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public Map<String, Object> addUser(@RequestBody User user) {
        boolean result = userService.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "添加成功" : "添加失败");
        response.put("data", user);
        return response;
    }

    /**
     * 根据ID删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        boolean result = userService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "删除成功" : "删除失败");
        return response;
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Map<String, Object> updateUser(@RequestBody User user) {
        boolean result = userService.updateById(user);
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "更新成功" : "更新失败");
        return response;
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/get/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", user != null);
        response.put("data", user);
        return response;
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    public Map<String, Object> listAllUsers() {
        List<User> users = userService.list();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", users);
        response.put("total", users.size());
        return response;
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Map<String, Object> pageUsers(
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

        wrapper.orderByDesc("created_time");

        IPage<User> pageResult = userService.page(page, wrapper);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", pageResult.getRecords());
        response.put("total", pageResult.getTotal());
        response.put("current", pageResult.getCurrent());
        response.put("size", pageResult.getSize());
        response.put("pages", pageResult.getPages());

        return response;
    }

    /**
     * 条件查询用户
     */
    @GetMapping("/query")
    public Map<String, Object> queryUsers(
            @RequestParam(required = false) Byte userType,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) Byte gender) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (userType != null) {
            wrapper.eq("user_type", userType);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (gender != null) {
            wrapper.eq("gender", gender);
        }

        wrapper.orderByDesc("created_time");

        List<User> users = userService.list(wrapper);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", users);
        response.put("total", users.size());

        return response;
    }

    /**
     * 统计用户数量
     */
    @GetMapping("/count")
    public Map<String, Object> countUsers(
            @RequestParam(required = false) Byte userType,
            @RequestParam(required = false) Byte status) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (userType != null) {
            wrapper.eq("user_type", userType);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }

        long count = userService.count(wrapper);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);

        return response;
    }
}
