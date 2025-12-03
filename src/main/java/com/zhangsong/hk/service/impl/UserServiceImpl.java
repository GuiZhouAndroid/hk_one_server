package com.zhangsong.hk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangsong.hk.entity.User;
import com.zhangsong.hk.mapper.UserMapper;
import com.zhangsong.hk.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
