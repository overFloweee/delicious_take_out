package com.hjw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjw.common.Result;
import com.hjw.dto.SetmealDto;
import com.hjw.pojo.Category;
import com.hjw.pojo.Dish;
import com.hjw.pojo.Setmeal;
import com.hjw.pojo.SetmealDish;
import com.hjw.service.CategoryService;
import com.hjw.service.DishService;
import com.hjw.service.SetmealDishService;
import com.hjw.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController
{
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal)
    {
        Long id = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus, status);
        queryWrapper.eq(Setmeal::getCategoryId, id);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);



        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<Page> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize, String name)
    {

        // 分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        wrapper.like(name != null, Setmeal::getName, name);

        setmealService.page(pageInfo, wrapper);

        // 使用dto 进行数据传递
        Page<SetmealDto> pageDto = new Page<>();
        // 进行 分页器 数据拷贝
        BeanUtils.copyProperties(pageInfo, pageDto, "records");
        // 拷贝属性
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = new ArrayList<>();
        for (Setmeal record : records)
        {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);

            Long categoryId = record.getCategoryId();
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(categoryId != null, Category::getId, categoryId);
            String categoryName = categoryService.getOne(queryWrapper).getName();

            setmealDto.setCategoryName(categoryName);
            list.add(setmealDto);
        }
        pageDto.setRecords(list);


        return Result.success(pageDto);
    }


    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto)
    {
        setmealService.saveWithDish(setmealDto);

        return Result.success("新增套餐成功！");
    }

    // 批量删除
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids)
    {
        setmealService.removeWithDish(ids);
        return Result.success("套餐删除成功！");
    }

    // 修改售卖状态 为 停售
    @PostMapping("/status/0")
    public Result<String> updateStatusFalse(@RequestParam List<Long> ids)
    {
        setmealService.updateStatusFalse(ids);
        return Result.success("停售成功！");
    }


    // 修改售卖状态 为 启售
    @PostMapping("/status/1")
    public Result<String> updateStatusTrue(@RequestParam List<Long> ids)
    {
        setmealService.updateStatusTrue(ids);
        return Result.success("启售成功！");
    }
}
