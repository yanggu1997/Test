package com.wzy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzy.reggie.common.BaseContext;
import com.wzy.reggie.common.R;
import com.wzy.reggie.pojo.ShoppingCart;
import com.wzy.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据{}", shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //若当前购物车已存在商品，点击添加则数量+1
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        //判断是否为菜品
        queryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId, shoppingCart.getDishId());
        //判断是否为套餐
        queryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        if(shoppingCartService.count(queryWrapper)>0){
//            List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
            ShoppingCart s1 =shoppingCartService.getOne(queryWrapper);
            int num = s1.getNumber()+1;
            s1.setNumber(num);
            shoppingCartService.updateById(s1);
            return R.success(s1);
        }
        else {
            shoppingCart.setNumber(1);
            //实体类加入自动注入,但无updatetime
           shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            return R.success(shoppingCart);
        }
    }
    @PostMapping("/sub")
    public R<String> Sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //若当前购物车已存在商品，点击添加则数量+1
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        //判断是否为菜品
        queryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId, shoppingCart.getDishId());
        //判断是否为套餐
        queryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        if(shoppingCartService.count(queryWrapper)>0){
            ShoppingCart s1 = shoppingCartService.list(queryWrapper).get(0);
            if (s1.getNumber()==1){
                shoppingCartService.removeById(s1.getId());
                return R.success("菜品减少成功");
            }
           else {
               int num = s1.getNumber()-1;
               s1.setNumber(num);
               shoppingCartService.updateById(s1);
                return R.success("菜品减少成功");
            }
        }
        return R.error("未查询到数据，减少异常");
    }

    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空菜品成功");
    }


}
