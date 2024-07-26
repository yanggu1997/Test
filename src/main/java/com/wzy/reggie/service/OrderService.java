package com.wzy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzy.reggie.pojo.Orders;

public interface OrderService extends IService<Orders> {


    public void submit(Orders orders);
}
