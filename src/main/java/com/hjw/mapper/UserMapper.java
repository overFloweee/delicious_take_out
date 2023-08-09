package com.hjw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjw.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>
{

}
