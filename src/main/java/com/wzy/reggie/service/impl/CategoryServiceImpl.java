package com.wzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzy.reggie.common.CustomException;
import com.wzy.reggie.mapper.CategoryMapper;
import com.wzy.reggie.pojo.Category;
import com.wzy.reggie.pojo.Dish;
import com.wzy.reggie.pojo.Setmeal;
import com.wzy.reggie.service.CategoryService;
import com.wzy.reggie.service.DishService;
import com.wzy.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements  CategoryService{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     *根据id删除分类，删除之前进行判断是否关联菜品和套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出异常
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();
        queryWrapper.eq(Dish::getCategoryId, id);
        int count1=dishService.count(queryWrapper);
        if(count1>0){
            throw  new CustomException("当前分类下已关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出异常
        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<Setmeal>();
        queryWrapper2.eq(Setmeal::getCategoryId, id);
        int count2=setmealService.count(queryWrapper2);
        if(count2>0){
            throw  new CustomException("当前分类下已关联了套餐，不能删除");
        }
        //正常删除
        super.removeById(id);
    }

}
