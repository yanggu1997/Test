package com.wzy.reggie;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class JedisTest {
    @Test
    public void test() {
        //获取链接
        Jedis jedis = new Jedis("localhost",6379);

        //
        jedis.set("username", "wzy");
        jedis.set("password", "1234");
        String value = jedis.get("username");
        System.out.println(value);
        jedis.close();

    }
}
