package com.hjw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hjw.common.Result;
import com.hjw.pojo.ShoppingCart;
import com.hjw.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController
{

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(HttpSession session)
    {
        Object userId = session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session)
    {

        // 设置用户id
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);


        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 确定 是哪个用户，对比用户id
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null)   // 是菜品
        {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }
        else                  // 是套餐
        {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // 查询 当前菜品或者套餐 是否已经在购物车中
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if (one != null)
        {
            // 已经在购物车中
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        }
        else
        {
            // 不在购物车中
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }


        return Result.success(one);
    }

    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session)
    {
        // 设置用户id
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 确定 是哪个用户，对比用户id
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null)   // 是菜品
        {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }
        else                  // 是套餐
        {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        Integer number = one.getNumber();
        if (number == 1)
        {
            shoppingCartService.remove(queryWrapper);
            one.setNumber(0);
        }
        else
        {
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }

        return Result.success(one);


    }

    @DeleteMapping("/clean")
    public Result<String> clean(HttpSession session)
    {
        Object userId = session.getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(wrapper);
        return Result.success("清空购物车成功！");
    }

}
