package com.hjw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjw.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish>
{
}
