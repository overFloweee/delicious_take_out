package com.hjw.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjw.common.Result;
import com.hjw.pojo.Category;
import com.hjw.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController
{
    @Autowired
    private CategoryService categoryService;

    // 分页查询
    @GetMapping("/page")
    public Result<Page> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize)
    {
        log.info("分页查询，当前是第 {} 页，每页 {} 条数据 ", page, pageSize);

        // 构造 分页构造器
        Page<Category> pageData = new Page<>();
        pageData.setCurrent(page);
        pageData.setSize(pageSize);

        // 构造 条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        // 添加 排序规则
        wrapper.orderByAsc(Category::getSort);

        // 执行查询
        categoryService.page(pageData, wrapper);

        return Result.success(pageData);
    }

    // 新增菜品分类
    @PostMapping
    public Result<String> save(@RequestBody Category category)
    {
        categoryService.save(category);
        return Result.success("新增菜品成功！");
    }

    // 修改 分类数据
    @PutMapping
    public Result<String> update(@RequestBody Category category)
    {
        categoryService.updateById(category);
        return Result.success("修改成功！");
    }

    // 删除 分类数据
    @DeleteMapping
    public Result<String> delete(Long ids)
    {
        categoryService.remove(ids);

        return Result.success("分类信息删除成功！");
    }


    // 在新增套餐页面 展示 已有套餐数据
    @GetMapping("/list")
    public Result<List<Category>> list(Category category)
    {

        // 条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        // 添加条件
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 添加排序条件
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // 条件查询
        List<Category> list = categoryService.list(wrapper);

        return Result.success(list);

    }
}
