package com.hjw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjw.dto.DishDto;
import com.hjw.pojo.Dish;
import com.hjw.pojo.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService extends IService<Dish>
{
    void saveWithFlavor(DishDto dishDto);

    DishDto getByidWithFlavor(String id);

    void updateWithFlavor(DishDto dishDto);

    void updateStatusFalse(List<Long> ids);

    void updateStatusTrue(List<Long> ids);

    void removeWithFlavor(List<Long> ids);
}
