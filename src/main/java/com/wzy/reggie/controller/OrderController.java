package com.wzy.reggie.controller;

import com.wzy.reggie.common.R;
import com.wzy.reggie.pojo.Orders;
import com.wzy.reggie.service.OrderDetailService;
import com.wzy.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private  OrderService orderService;
    @Autowired
    private OrderDetailService  orderDetailService;
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders orders) {
        log.info("订单数据{}",orders);
        orderService.submit(orders);
        return null;
    }
}
