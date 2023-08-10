package com.hjw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjw.common.CustomException;
import com.hjw.dto.DishDto;
import com.hjw.mapper.DishMapper;
import com.hjw.pojo.Dish;
import com.hjw.pojo.DishFlavor;
import com.hjw.service.DishFlavorService;
import com.hjw.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService
{

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private HttpSession session;

    // 多表操作, 需要开启事务
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto)
    {
        // mp的save方法会自动 产生数据回显，将主键id 等 返回 至原对象
        this.save(dishDto);


        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors)
        {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public DishDto getByidWithFlavor(String id)
    {
        DishDto dishDto = new DishDto();

        // 查询数据
        Dish dish = this.getById(id);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(wrapper);

        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto)
    {
        // 更新dish表中的数据
        this.updateById(dishDto);
        // 需要先执行flavor表 相应 的delete操作
        Long dishId = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dishId != null, DishFlavor::getDishId, dishId);
        dishFlavorService.remove(wrapper);

        // 执行flavor表 相应的 update操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 缺少 dishId属性，单独添加
        for (DishFlavor flavor : flavors)
        {
            flavor.setDishId(dishId);
        }

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void updateStatusFalse(List<Long> ids)
    {
        // 查询售卖状态，是否都为 启售状态
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 0);

        int count = this.count(queryWrapper);
        if (count > 0)
        {
            throw new CustomException("已有部分菜品为停售状态！");
        }

        // 如果都为启售状态，则批量停售
        LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Dish::getId, ids);
        wrapper.set(Dish::getStatus, 0);

        this.update(wrapper);
    }

    @Override
    @Transactional
    public void updateStatusTrue(List<Long> ids)
    {
        // 查询售卖状态，是否都为 停售状态
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0)
        {
            throw new CustomException("已有部分菜品为启售状态！");
        }

        // 如果都为启售状态，则批量启售
        LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Dish::getId, ids);
        wrapper.set(Dish::getStatus, 1);

        this.update(wrapper);

    }

    @Override
    @Transactional   // 批量删除 菜品 及 关联的口味
    public void removeWithFlavor(List<Long> ids)
    {
        // 先判断 售卖状态是否为 停售，否则不能删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0)
        {
            throw new CustomException("菜品为启售状态，不能删除！");
        }

        // 如果为 停售状态，则 批量删除
        this.removeByIds(ids);

        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(wrapper);

    }
}
