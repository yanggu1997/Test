package com.wzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzy.reggie.common.CustomException;
import com.wzy.reggie.common.R;
import com.wzy.reggie.dto.DishDto;
import com.wzy.reggie.mapper.DishMapper;
import com.wzy.reggie.pojo.Dish;
import com.wzy.reggie.pojo.DishFlavor;
import com.wzy.reggie.service.DishFlavorService;
import com.wzy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;
    /**
     * 新增菜品同事保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
       this.save(dishDto);
        Long dishid = dishDto.getId();
        List<DishFlavor> flavors=dishDto.getFlavors();
//        flavors = flavors.stream().map((item) -> {
//            item.setDishId(dishid);
//            return item;
//        }).collect(Collectors.toList());
//        dishFlavorService.saveBatch(flavors);
        for(DishFlavor df:flavors){
            df.setDishId(dishid);
            //批量保存集合savabatch，
            dishFlavorService.save(df);

        }



    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        Long dishid = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishid);
        dishFlavorService.remove(queryWrapper);
//        dishFlavorService.removeById(dishid);//dish id无法和数据库中的 id对应
        List<DishFlavor> flavors=dishDto.getFlavors();
        for(DishFlavor df:flavors){
            df.setDishId(dishid);
            dishFlavorService.save(df);
        }

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors=dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    public void updateDishStatus(Integer status, List<Long> id) {

       LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getId,id);
        if(queryWrapper==null){
            R.error("数据异常，未查询到此菜");
        }
        List <Dish> dishList=this.list(queryWrapper);
//
       List<Dish> dishs =new ArrayList<>();
        for(Long dishid :id){
            Dish dish =new Dish();
            dish.setId(dishid);
            dish.setStatus(status);
            dishs.add(dish);
        }
        updateBatchById(dishs);
    }

    @Override
    public void removeWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("当前菜品正在售卖");
        }
        //若count=0则删除
        this.removeByIds(ids);

        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);
    }


}
