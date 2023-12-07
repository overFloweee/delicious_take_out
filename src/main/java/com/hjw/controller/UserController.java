package com.hjw.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hjw.common.Result;
import com.hjw.pojo.User;
import com.hjw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController
{
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session)
    {

        // 判断是否是新用户
        String phone = (String) map.get("phone");
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User one = userService.getOne(wrapper);

        if (one == null)
        {
            // 新用户，自动完成注册
            one = new User();
            one.setPhone(phone);
            one.setStatus(1);
            String name = RandomUtil.randomString("abcdefghijklmnopqrstuvwxyz1234567890", 6);
            one.setName(name);
            userService.save(one);
        }

        // 将用户id放进session
        session.setAttribute("user", one.getId());
        return Result.success(one);

    }


    @PostMapping("/loginout")
    public Result<String> loginout(HttpSession session)
    {
        session.removeAttribute("user");
        return Result.success("退出登陆成功!");
    }

}
