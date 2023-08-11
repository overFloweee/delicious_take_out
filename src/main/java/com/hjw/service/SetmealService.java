package com.hjw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjw.dto.DishDto;
import com.hjw.dto.SetmealDto;
import com.hjw.pojo.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal>
{
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    void updateStatusFalse(List<Long> ids);

    void updateStatusTrue(List<Long> ids);

    SetmealDto getByidWithDish(String id);

    void updateWithDish(SetmealDto setmealDto);
}
