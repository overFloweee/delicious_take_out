package com.hjw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjw.mapper.UserMapper;
import com.hjw.pojo.User;
import com.hjw.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
}
