package com.hjw.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjw.common.CustomException;
import com.hjw.mapper.CategoryMapper;
import com.hjw.mapper.DishMapper;
import com.hjw.pojo.Category;
import com.hjw.pojo.Dish;
import com.hjw.pojo.Setmeal;
import com.hjw.service.CategoryService;
import com.hjw.service.DishService;
import com.hjw.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatetoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService
{

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    // 根据id删除分类，先要判断分类是否 关联了 菜品或者套餐
    // 如果已经关联，则抛出 一个 业务异常
    @Override
    public void remove(Long id)
    {
        // 查询 菜品 是否关联了 菜品分类的id
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, id);
        int dish = dishService.count(dishWrapper);

        if (dish > 0)
        {
            throw new CustomException("当前分类下关联了菜品，不能删除！");
        }

        // 查询 套餐 是否关联了 套餐分类的id
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        int setmeal = setmealService.count(setmealWrapper);

        if (setmeal > 0)
        {
            throw new CustomException("当前分类下关联了套餐，不能删除！");
        }

        super.removeById(id);

    }
}
