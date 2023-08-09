package com.hjw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjw.common.CustomException;
import com.hjw.dto.SetmealDto;
import com.hjw.mapper.SetmealMapper;
import com.hjw.pojo.Setmeal;
import com.hjw.pojo.SetmealDish;
import com.hjw.service.SetmealDishService;
import com.hjw.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService
{
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto)
    {
        // mp的save方法会自动 产生数据回显，将主键id 等 返回 至原对象
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealDtoId = setmealDto.getId();
        for (SetmealDish setmealDish : setmealDishes)
        {
            setmealDish.setSetmealId(setmealDtoId);
        }

        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional  // 需要操作两张表，套餐 和 菜品 的关联关系
    public void removeWithDish(List<Long> ids)
    {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        // 查询批量删除的 套餐 是否都处于停售状态
        int count = this.count(queryWrapper);
        if (count > 0)
        {
            throw new CustomException("套餐正在售卖中，不能删除！");
        }

        // 如果可以删除，则批量删除
        for (Long id : ids)
        {
            // 删除 套餐
            this.removeById(id);

            // 删除 套餐关联的菜品
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getSetmealId, id);
            setmealDishService.remove(wrapper);
        }

    }

    @Override
    public void updateStatusFalse(List<Long> ids)
    {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 0);

        int count = this.count(queryWrapper);
        if (count > 0)
        {
            throw new CustomException("选中的套餐中已有部分停售！");
        }

        // 批量停售
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId, ids);
        updateWrapper.set(Setmeal::getStatus, 0);
        this.update(updateWrapper);


    }

    @Override
    public void updateStatusTrue(List<Long> ids)
    {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0)
        {
            throw new CustomException("选中的套餐中已有部分启售！");
        }

        // 批量起售
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId, ids);
        updateWrapper.set(Setmeal::getStatus, 1);
        this.update(updateWrapper);
    }
}
