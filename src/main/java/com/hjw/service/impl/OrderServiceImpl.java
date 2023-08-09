package com.hjw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjw.common.CustomException;
import com.hjw.mapper.OrderMapper;
import com.hjw.pojo.*;
import com.hjw.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService
{

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;


    /**
     * 用户下单
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders)
    {
        // 获取当前用户
        Long userId = (Long) session.getAttribute("user");

        // 获取用户 购物车 数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        // 如果购物车数据为空
        if (shoppingCartList.size() == 0)
        {
            throw new CustomException("购物车为空！");
        }

        // 下单 -> 向 订单表插入一条数据（地址）
        // 设置 用户id
        orders.setUserId(userId);
        // 设置 订单地址
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null)
        {
            throw new CustomException("地址信息为空！");
        }
        orders.setAddress(addressBook.toString());
        // 生成 订单号
        long orderId = IdWorker.getId();
        orders.setNumber(String.valueOf(orderId));


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setUserId(userId);
        // 设置用户名
        User user = userService.getById(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()) + (addressBook.getCityName() == null ? "" : addressBook.getCityName()) + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()) + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));


        // 计算总金额 同时 包装订单明细表
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) ->
        {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orders.setAmount(new BigDecimal(amount.get()));// 总金额

        this.save(orders);

        // 向 订单明细表插入数据（商品内容）
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车数据
        shoppingCartService.remove(queryWrapper);

    }


    // @Transactional
    // public void submit(Orders orders) {
    //     //获得当前用户id
    //     Long userId = BaseContext.getCurrentId();
    //
    //     //查询当前用户的购物车数据
    //     LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    //     wrapper.eq(ShoppingCart::getUserId,userId);
    //     List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
    //
    //     if(shoppingCarts == null || shoppingCarts.size() == 0){
    //         throw new CustomException("购物车为空，不能下单");
    //     }
    //
    //     //查询用户数据
    //     User user = userService.getById(userId);
    //
    //     //查询地址数据
    //     Long addressBookId = orders.getAddressBookId();
    //     AddressBook addressBook = addressBookService.getById(addressBookId);
    //     if(addressBook == null){
    //         throw new CustomException("用户地址信息有误，不能下单");
    //     }
    //
    //     long orderId = IdWorker.getId();//订单号
    //
    //     AtomicInteger amount = new AtomicInteger(0);
    //
    //     List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
    //         OrderDetail orderDetail = new OrderDetail();
    //         orderDetail.setOrderId(orderId);
    //         orderDetail.setNumber(item.getNumber());
    //         orderDetail.setDishFlavor(item.getDishFlavor());
    //         orderDetail.setDishId(item.getDishId());
    //         orderDetail.setSetmealId(item.getSetmealId());
    //         orderDetail.setName(item.getName());
    //         orderDetail.setImage(item.getImage());
    //         orderDetail.setAmount(item.getAmount());
    //         amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
    //         return orderDetail;
    //     }).collect(Collectors.toList());
    //
    //
    //     orders.setId(orderId);
    //     orders.setOrderTime(LocalDateTime.now());
    //     orders.setCheckoutTime(LocalDateTime.now());
    //     orders.setStatus(2);
    //     orders.setAmount(new BigDecimal(amount.get()));//总金额
    //     orders.setUserId(userId);
    //     orders.setNumber(String.valueOf(orderId));
    //     orders.setUserName(user.getName());
    //     orders.setConsignee(addressBook.getConsignee());
    //     orders.setPhone(addressBook.getPhone());
    //     orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
    //             + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
    //             + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
    //             + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
    //     //向订单表插入数据，一条数据
    //     this.save(orders);
    //
    //     //向订单明细表插入数据，多条数据
    //     orderDetailService.saveBatch(orderDetails);
    //
    //     //清空购物车数据
    //     shoppingCartService.remove(wrapper);
    // }
}