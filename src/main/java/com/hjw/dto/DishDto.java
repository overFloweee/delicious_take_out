package com.hjw.dto;

import com.hjw.pojo.Dish;
import com.hjw.pojo.DishFlavor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DishDto extends Dish
{
    private List<DishFlavor> flavors;
    private String categoryName;
    private Integer copies;
}
