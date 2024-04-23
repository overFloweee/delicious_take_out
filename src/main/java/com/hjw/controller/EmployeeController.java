package com.hjw.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjw.common.Result;
import com.hjw.pojo.Employee;
import com.hjw.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController
{

    @Autowired
    private EmployeeService employeeService;

    // 员工登陆
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee)
    {
        log.info("员工登陆：{}", employee);
        // 1、将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3、如果没有查询到则返回登陆失败的结果
        if (emp == null)
        {
            return Result.error("登陆失败！");
        }

        // 4、进行密码的比对，如果不一致则返回登陆失败的结果
        if (!emp.getPassword().equals(password))
        {
            return Result.error("登陆失败！");
        }

        // 5、查看员工状态，如果为禁用状态则返回登陆失败结果
        if (emp.getStatus() == 0)
        {
            return Result.error("账号已禁用！");
        }

        // 6、登陆成功，将员工id存入Session并返回登陆成功结果
        HttpSession session = request.getSession();
        session.setAttribute("employee", emp.getId());
        session.setMaxInactiveInterval(3600);
        log.info(session.getId());


        return Result.success(emp).add("JESSIONID", session.getId());
    }

    // 员工退出账号
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String id = session.getId();
        log.info(id);
        // 清理session保存的员工id
        session.removeAttribute("employee");


        return Result.success("退出成功！");
    }


    @PostMapping
    public Result<String> save(@RequestBody Employee employee)
    {
        log.info("新增员工： {} ", employee);
        // 设置初始密码 : 123456
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

        employeeService.save(employee);

        return Result.success("新增员工成功！");

    }

    // 分页查询
    @GetMapping("/page")
    public Result<Page> page(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize, String name
    )
    {
        log.info("分页查询，当前是第 {} 页，每页 {} 条数据 ", page, pageSize);

        // 构造 分页构造器
        Page<Employee> pageData = new Page<>();
        pageData.setCurrent(page);
        pageData.setSize(pageSize);

        // 构造 条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Employee::getName, name);
        // 添加 排序规则
        wrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageData, wrapper);

        return Result.success(pageData);
    }


    // 根据id 修改员工信息
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee)
    {
        employeeService.updateById(employee);
        return Result.success("员工信息修改成功！");
    }


    // 编辑员工信息前的 回显数据
    @GetMapping("/{id}")
    public Result<Employee> update(@PathVariable Long id)
    {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getId, id);
        Employee emp = employeeService.getOne(wrapper);
        log.info("即将编辑的员工数据为 ： {} ", emp);
        return Result.success(emp);

    }


    @GetMapping("/getInfo/{id}")
    public Result<Employee> getInfo(@PathVariable Long id)
    {
        return Result.success(employeeService.getById(id));

    }
}
