package com.wzy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzy.reggie.dto.DishDto;
import com.wzy.reggie.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品 同时插入菜品对应的口味数据
    public  void saveWithFlavor(DishDto dishDto);
    public  void updateWithFlavor(DishDto dishDto);
    public  DishDto getByIdWithFlavor(Long id);

   public  void updateDishStatus(Integer status,List<Long> id);

    public void removeWithFlavor(List<Long> ids);
}
