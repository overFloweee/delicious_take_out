package com.hjw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjw.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}