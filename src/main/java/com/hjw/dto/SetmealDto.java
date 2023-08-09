package com.hjw.dto;

import com.hjw.pojo.Setmeal;
import com.hjw.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal
{

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
