package com.hjw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjw.mapper.SetmealDishMapper;
import com.hjw.pojo.SetmealDish;
import com.hjw.service.SetmealDishService;
import com.hjw.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService
{
}
