package com.hjw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjw.mapper.DishFlavorMapper;
import com.hjw.pojo.DishFlavor;
import com.hjw.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService
{
}
