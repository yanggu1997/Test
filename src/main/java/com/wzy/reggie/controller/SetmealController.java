package com.wzy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzy.reggie.common.R;
import com.wzy.reggie.dto.SetmealDto;
import com.wzy.reggie.pojo.Category;
import com.wzy.reggie.pojo.Setmeal;
import com.wzy.reggie.pojo.SetmealDish;
import com.wzy.reggie.service.CategoryService;
import com.wzy.reggie.service.SetmealDishService;
import com.wzy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private  SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/page")//前端请求方式是get请求
    public R<Page> page(int page, int pageSize, String name){
        log.info("page{},pagesize={}",page,pageSize);
        //构造分页构造器
        Page<Setmeal> pageInfo =new Page<>(page,pageSize);
        Page<SetmealDto> dtopageInfo =new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //排序条件
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtopageInfo,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list =records.stream().map((item)->{
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item,dto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryname = category.getName();
                dto.setCategoryName(categoryname);
            }
            return dto;
        }).collect(Collectors.toList());

        dtopageInfo.setRecords(list);

        return R.success(dtopageInfo);
    }

    @GetMapping("/list")
    public R<List<Setmeal>> getAll(Setmeal setmeal){
        log.info("add{}",setmeal);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        return R.success(setmeals);

    }
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("setmealDto={}",setmealDto);
        //条件构造器
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
}

    @DeleteMapping
    public R<String> deletesetmeal(@RequestParam List<Long> ids) {
        log.info("ids={}",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,@RequestParam List<Long> ids) {
        log.info("ids={}",ids);
        setmealService.updateStatus(status,ids);
        return R.success("修改成功");
    }
    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> setMealDishDetails(SetmealDish setmealDish) {
        log.info("setmealDish={}",setmealDish);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmealDish.getSetmealId()!=null,SetmealDish::getSetmealId,setmealDish.getSetmealId());
        queryWrapper.gt(SetmealDish::getCopies,0);
        queryWrapper.orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        return R.success(list);

    }
}
