package com.wzy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzy.reggie.common.R;
import com.wzy.reggie.dto.DishDto;
import com.wzy.reggie.pojo.Category;
import com.wzy.reggie.pojo.Dish;
import com.wzy.reggie.pojo.DishFlavor;
import com.wzy.reggie.service.CategoryService;
import com.wzy.reggie.service.DishFlavorService;
import com.wzy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/page")//前端请求方式是get请求
    public R<Page> page(int page, int pageSize,String name){
        log.info("page{},pagesize={}",page,pageSize);
        //构造分页构造器
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        Page<DishDto> dtopageInfo =new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //排序条件
        queryWrapper.like(name != null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtopageInfo,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list =records.stream().map((item)->{
            DishDto dto = new DishDto();
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

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        String keys="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(keys);
        return R.success("新增成功");
    }
    @PutMapping
    public R<String> updateWithFlavor(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        //Set keys=redisTemplate.keys("dish_*");清理所有
        String keys="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(keys);
        return R.success("修改成功");
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> search(@PathVariable Long id){
        //@PathVariable 注解的作用是将 URL 中的路径参数（Path parameters）绑定到方法的参数上。在 Spring MVC 中，我们可以通过在控制器（Controller）的方法参数上添加 @PathVariable 注解来获取 URL 中的变量值，并将其作为方法参数的值进行使用。

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PostMapping("status/{status}")
        public R<String> updateStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        log.info("套餐修改状态为{},ids={}",status,ids);
        dishService.updateDishStatus(status,ids);
        Set keys=redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("修改成功");
    }


    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        dishService.removeWithFlavor(ids);
        Set keys=redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("删除套餐成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> getAll(Dish dish){
        List<DishDto> dishDtos=null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        //动态构造key
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //(Dish dish)方便dish类的其他参数，不止是id参数还可以接受name等，要求id和类中的id名称一致
        if(dishDtos!=null){
            return R.success(dishDtos);
        }
        //如果不存在
        log.info("dish={}",dish);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(dish.getName()), Dish::getName, dish.getName());
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> dishs = dishService.list(queryWrapper);
//        return R.success(dishs);
         dishDtos = dishs.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());

            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, item.getId());

            dishDto.setFlavors(dishFlavorService.list(wrapper));
            return dishDto;
        }).collect(Collectors.toList());
        //如果不存在，讲查询到的数据缓存到redis
        redisTemplate.opsForValue().set(key,dishDtos,60, TimeUnit.MINUTES);

        return R.success(dishDtos);
    }
}
