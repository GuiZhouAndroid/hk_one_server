package com.zhangsong.hk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhangsong.hk.entity.Role;
import com.zhangsong.hk.service.IRoleService;
import com.zhangsong.hk.utils.exception.CustomException;
import com.zhangsong.hk.utils.response.R;
import com.zhangsong.hk.utils.response.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: RoleController
 * @Description: 角色管理控制器
 * @Author: 张松
 * @Date: 2025/12/8 20:12
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/role")
@Validated
@SaCheckLogin
public class RoleController {

    @Autowired
    private IRoleService roleService;

    /**
     * 获取所有可用角色
     * @return 角色列表
     */
    @GetMapping("/list")
    public R listAllRoles() {
        List<Role> roles = roleService.getAllActiveRoles();
        return R.ok().data(roles);
    }

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public R getRoleById(@PathVariable Integer id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new CustomException(ResultCodeEnum.NOT_FOUND.getCode(), "角色不存在");
        }
        return R.ok().data(role);
    }

    /**
     * 创建角色（需要管理员权限）
     * @param role 角色信息
     * @return 创建结果
     */
    @PostMapping("/create")
    @SaCheckRole("ADMIN")
    public R createRole(@RequestBody Role role) {
        role.setCreatedTime(LocalDateTime.now());
        role.setStatus((byte) 1);
        
        boolean result = roleService.save(role);
        if (!result) {
            throw new CustomException(ResultCodeEnum.FAIL.getCode(), "创建角色失败");
        }
        return R.ok("角色创建成功").data(role);
    }

    /**
     * 更新角色（需要管理员权限）
     * @param role 角色信息
     * @return 更新结果
     */
    @PutMapping("/update")
    @SaCheckRole("ADMIN")
    public R updateRole(@RequestBody Role role) {
        if (role.getId() == null) {
            throw new CustomException(ResultCodeEnum.BAD_REQUEST.getCode(), "角色ID不能为空");
        }
        
        boolean result = roleService.updateById(role);
        if (!result) {
            throw new CustomException(ResultCodeEnum.FAIL.getCode(), "更新角色失败");
        }
        return R.ok("角色更新成功");
    }

    /**
     * 禁用角色（需要管理员权限）
     * @param id 角色ID
     * @return 禁用结果
     */
    @PutMapping("/disable/{id}")
    @SaCheckRole("ADMIN")
    public R disableRole(@PathVariable Integer id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new CustomException(ResultCodeEnum.NOT_FOUND.getCode(), "角色不存在");
        }
        
        role.setStatus((byte) 0);
        boolean result = roleService.updateById(role);
        if (!result) {
            throw new CustomException(ResultCodeEnum.FAIL.getCode(), "禁用角色失败");
        }
        return R.ok("角色已禁用");
    }

    /**
     * 分页查询角色
     * @param current 当前页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    @GetMapping("/page")
    @SaCheckRole("ADMIN")
    public R pageRoles(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        
        Page<Role> page = new Page<>(current, size);
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("role_name", keyword)
                   .or()
                   .like("role_code", keyword);
        }
        
        wrapper.orderByDesc("created_time");
        IPage<Role> pageResult = roleService.page(page, wrapper);
        
        return R.ok().data(pageResult);
    }
}