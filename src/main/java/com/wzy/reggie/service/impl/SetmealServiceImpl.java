package com.wzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzy.reggie.common.CustomException;
import com.wzy.reggie.dto.SetmealDto;
import com.wzy.reggie.mapper.SetmealMapper;
import com.wzy.reggie.pojo.Setmeal;
import com.wzy.reggie.pojo.SetmealDish;
import com.wzy.reggie.service.SetmealDishService;
import com.wzy.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDish= setmealDto.getSetmealDishes();
        //getSetmealDishes()将SetmealDishe的元素已经获得，但并没有SetmealId，故需要遍历获得。
        for(SetmealDish s1 :setmealDish){
            s1.setSetmealId(setmealDto.getId());
            setmealDishService.save(s1);
        }
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
            List<Setmeal> setmeal= setmealService.listByIds(ids);
            for(Long id :ids){
                Setmeal s1 = new Setmeal();
                s1.setId(id);
                s1.setStatus(status);
                setmealService.updateById(s1);
            }
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //ids是数组故无法使用eq
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中，无法删除");
        }
        //如果等于0，可以删除
        this.removeByIds(ids);

        //删除setmealdish中的菜品

        //  setmealDishService.removeByIds(ids);无法使用，ids并未对应setmealDish表中的id，而是对应setmealid
        LambdaQueryWrapper<SetmealDish> queryWrapperDish = new LambdaQueryWrapper<>();
        //ids是数组故无法使用eq，用in来代替for循环
        queryWrapperDish.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapperDish);

    }


}
