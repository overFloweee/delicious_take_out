package com.hjw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjw.common.Result;
import com.hjw.dto.DishDto;
import com.hjw.pojo.Category;
import com.hjw.pojo.Dish;
import com.hjw.pojo.DishFlavor;
import com.hjw.service.CategoryService;
import com.hjw.service.DishFlavorService;
import com.hjw.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DIshController
{

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    // 分页查询
    @GetMapping("/page")
    public Result<Page> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize, String name)
    {
        // 分页构造器
        Page<Dish> dishPage = new Page<>();
        dishPage.setSize(pageSize);
        dishPage.setCurrent(page);
        Page<DishDto> dishDtoInfo = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(dishPage, wrapper);

        // 对象拷贝
        BeanUtils.copyProperties(dishPage, dishDtoInfo, "records");  // copy 忽略 records属性

        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = new ArrayList<>();

        for (Dish record : records)
        {
            // 对象拷贝
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);

            // 根据 菜品id 查询 得到菜品名称
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null)
            {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            // 设置菜品名称
            list.add(dishDto);

        }

        dishDtoInfo.setRecords(list);
        return Result.success(dishDtoInfo);
    }


    @PostMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> save(@RequestBody DishDto dishDto)
    {
        dishService.saveWithFlavor(dishDto);

        return Result.success("新增菜品成功！");
    }


    // 数据回显
    @GetMapping("/{id}")
    public Result<DishDto> query(@PathVariable String id)
    {
        // 需要查询两张表
        DishDto dishDto = dishService.getByidWithFlavor(id);

        return Result.success(dishDto);

    }

    // 修改菜品
    @PutMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> update(@RequestBody DishDto dishDto)
    {
        // 两张表 Dish 和 DishFlavor 的更新
        dishService.updateWithFlavor(dishDto);

        return Result.success("修改菜品成功！");
    }

    // 数据展示
    @GetMapping("/list")
    @Cacheable(value = "dishCache", key = "#dish.categoryId + '_' + #dish.status")
    public Result<List<DishDto>> list(Dish dish)
    {

        Long categoryId = dish.getCategoryId();

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(wrapper);

        // 包装dto对象
        List<DishDto> dishDtoList = list.stream().map((item) ->
        {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            // 查询口味
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, id);
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

            dishDto.setFlavors(flavors);

            return dishDto;
        }).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }

    // 修改售卖状态 为 停售
    @PostMapping("/status/0")
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> updateStatusFalse(@RequestParam List<Long> ids)
    {
        dishService.updateStatusFalse(ids);
        return Result.success("停售成功！");
    }

    // 修改售卖状态 为 启售
    @PostMapping("/status/1")
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> updateStatusTrue(@RequestParam List<Long> ids)
    {
        dishService.updateStatusTrue(ids);
        return Result.success("启售成功！");
    }

    // 批量删除
    @DeleteMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result<String> remove(@RequestParam List<Long> ids)
    {
        dishService.removeWithFlavor(ids);

        return Result.success("菜品删除成功！");
    }


}
