package com.hjw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjw.common.Result;
import com.hjw.pojo.OrderDetail;
import com.hjw.pojo.Orders;
import com.hjw.service.OrderDetailService;
import com.hjw.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController
{
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private HttpSession session;


    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders)
    {

        orderService.submit(orders);
        return Result.success("支付成功！");
    }

    @GetMapping("/userPage")
    public Result<Page> userPage(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1") Long pageSize)
    {

        // 根据用户id 获取 订单id
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, session.getAttribute("user"));
        Orders order = orderService.getOne(queryWrapper);
        Long orderId = order.getId();

        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId, orderId);
        int count = orderDetailService.count(wrapper);

        // 分页构造器
        Page<OrderDetail> pageInfo = new Page<>(page, count);


        orderDetailService.page(pageInfo, wrapper);

        return Result.success(pageInfo);
    }

}
