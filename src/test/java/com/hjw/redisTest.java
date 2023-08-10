package com.hjw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class redisTest
{

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void te1()
    {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys)
        {
            System.out.println(key);
        }
        redisTemplate.delete(keys);
    }

    @Test
        // 字符串 Stirng
    void contextLoads()
    {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        opsForValue.set("city", "jiangxi");

        String city = opsForValue.get("city");
        System.out.println(city);

        opsForValue.set("username", "hjw", 10, TimeUnit.SECONDS);

    }

    @Test  // 哈希 Hash
    public void test1()
    {
        HashOperations<String, Object, Object> forHash = redisTemplate.opsForHash();
        forHash.put("001", "username", "hjw");
        forHash.put("001", "age", "20");

        // 取值
        System.out.println(forHash.get("001", "username"));

        // 获得hash结构中所有字段
        System.out.println(forHash.keys("001"));   // [username, age]

        // 获得 所有value
        System.out.println(forHash.values("001"));  // [hjw, 20]

        // 获得 所有字段 + value
        Map<Object, Object> entries = forHash.entries("001");
        entries.forEach((key, value) -> System.out.println(key + " : " + value));
        // username : hjw
        // age : 20

    }

    @Test  // 列表 List
    public void test2()
    {
        ListOperations<String, String> forList = redisTemplate.opsForList();

        forList.leftPush("mylist", "a");
        forList.leftPushAll("mylist", "b", "c", "a");

        List<String> mylist = forList.range("mylist", 0, -1);
        for (String s : mylist)
        {
            System.out.println(s);
        }

        // 全部 弹出 队列
        Long size = forList.size("mylist");
        int len = size.intValue();
        for (int i = 0; i < len; i++)
        {
            forList.rightPop("mylist");

        }

    }

    @Test  // 集合 Set
    public void test3()
    {
        SetOperations<String, String> forSet = redisTemplate.opsForSet();

        forSet.add("set1", "a", "b", "v", "x");
        forSet.add("set2", "a", "b", "c");

        System.out.println(forSet.members("set1"));

        forSet.remove("set1", "x");

        Set<String> difference = forSet.difference("set1", "set2");
        System.out.println(difference);

    }

    @Test  // 有序集合 ZSet
    public void test4()
    {

    }

    @Test  // 通用操作
    public void test5()
    {
        // 获取 redis中所有的key
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys)
        {
            System.out.println(key);
        }
        System.out.println("================================");
        // 判断 某个 key 是否存在
        Boolean usernameBoolean = redisTemplate.hasKey("username");
        System.out.println(usernameBoolean);


        // 删除指定的key
        redisTemplate.delete("�� \u0005t \u0004city");

        // 获取 key 对应的value的数据类型
        System.out.println(redisTemplate.type("set1"));
    }

}
