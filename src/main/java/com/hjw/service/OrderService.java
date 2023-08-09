package com.hjw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjw.pojo.Orders;
import org.springframework.stereotype.Service;

@Service
public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
