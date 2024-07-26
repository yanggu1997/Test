package com.wzy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.reggie.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper <Dish>{
}
