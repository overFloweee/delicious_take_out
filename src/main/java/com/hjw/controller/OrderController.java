package com.hjw.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjw.common.Result;
import com.hjw.dto.OrdersDto;
import com.hjw.dto.SetmealDto;
import com.hjw.pojo.OrderDetail;
import com.hjw.pojo.Orders;
import com.hjw.pojo.Setmeal;
import com.hjw.service.OrderDetailService;
import com.hjw.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
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


    // TODO：前台用户个人信息界面完善、显示

    @GetMapping("/userPage")
    public Result<Page> userPage(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1") Long pageSize)
    {

        // 根据用户id 获取 订单id
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, session.getAttribute("user"));
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        orderService.page(pageInfo, queryWrapper);

        // 使用dto 进行数据传递
        Page<OrdersDto> pageDto = new Page<>();
        BeanUtils.copyProperties(pageInfo, pageDto, "records");
        // 拷贝属性
        List<Orders> records = pageInfo.getRecords();
        ArrayList<OrdersDto> list = new ArrayList<>();
        for (Orders record : records)
        {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(record, ordersDto);

            // 获取订单id
            Long orderId = record.getId();

            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);


            ordersDto.setOrderDetails(orderDetails);
            list.add(ordersDto);
        }

        pageDto.setRecords(list);


        return Result.success(pageDto);
    }


    // 后台所有订单明细显示
    @GetMapping("/page")
    public Result<Page> orderPage(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1") Long pageSize, Long number, String beginTime, String endTime)
    {

        Page<Orders> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(number != null, Orders::getId, number);
        wrapper.gt(beginTime != null, Orders::getOrderTime, beginTime);
        wrapper.lt(endTime != null, Orders::getOrderTime, endTime);

        wrapper.orderByDesc(Orders::getOrderTime);


        orderService.page(pageInfo, wrapper);


        return Result.success(pageInfo);
    }


    // 修改配送状态
    @PutMapping
    public Result distribution(@RequestBody Orders orders)
    {

        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Orders::getStatus, orders.getStatus());
        wrapper.eq(Orders::getId, orders.getId());

        boolean isOk = orderService.update(wrapper);

        if (!isOk)
        {
            return Result.success("配送失败");

        }
        return Result.success(isOk);

    }


}
