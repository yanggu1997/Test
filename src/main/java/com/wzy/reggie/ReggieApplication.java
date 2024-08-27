package com.wzy.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//lombok提供，为实体类提供get set方法，还提供Slf4j方法，log方法 输出日志
@Slf4j
@SpringBootApplication
@ServletComponentScan//扫描WebFilter过滤器
@EnableCaching//开启spring cache注解缓存功能
//@EnableTransactionManagement
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        //@Slf4j提供 log方法
        log.info("项目启动成功！");
    }

}
