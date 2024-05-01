package com.hjw.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjw.common.Result;
import com.hjw.pojo.User;
import com.hjw.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("user")
@Slf4j
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

    @GetMapping("/test")
    public Result<String> test()
    {
        System.out.println("test...............................");
        return Result.success("测试成功！!");
    }

    // 分页查询
    @GetMapping("/page")
    public Result<Page> page(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize, String name
    )
    {
        log.info("分页查询，当前是第 {} 页，每页 {} 条数据 ", page, pageSize);

        // 构造 分页构造器
        Page<User> pageData = new Page<>();
        pageData.setCurrent(page);
        pageData.setSize(pageSize);

        // 构造 条件构造器
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, User::getName, name);
        // 添加 排序规则
        wrapper.orderByDesc(User::getId);

        // 执行查询
        userService.page(pageData, wrapper);

        return Result.success(pageData);
    }

    // 根据id 修改用户信息
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody User user)
    {
        userService.updateById(user);
        return Result.success("用户信息修改成功！");
    }

    @PostMapping
    public Result<String> save(@RequestBody User user)
    {
        log.info("新增用户： {} ", user);

        userService.save(user);

        return Result.success("新增员工成功！");

    }

    // 编辑员工信息前的 回显数据
    @GetMapping("/{id}")
    public Result<User> update(@PathVariable Long id)
    {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, id);
        User user = userService.getOne(wrapper);
        log.info("即将编辑的员工数据为 ： {} ", user);
        return Result.success(user);

    }

}
