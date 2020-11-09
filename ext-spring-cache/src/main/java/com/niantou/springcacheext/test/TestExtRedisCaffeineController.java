package com.niantou.springcacheext.test;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.Caffeine;
import com.niantou.springcacheext.cache.annotation.ExtCacheable;
import com.niantou.springcacheext.cache.annotation.Redis;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试 ext-caffeine
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 13:29:03
 */
@RestController
@RequestMapping("/redis-caffeine")
@SuppressWarnings("SpringCacheNamesInspection")
public class TestExtRedisCaffeineController {
    
    @GetMapping("/one")
    @Cacheable(cacheNames = "cache-name123", key = "'one' + #param1")
    public Object one(String param1) {
        System.err.println("进one了\t" + param1);
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
    
    @GetMapping("/two")
    @ExtCacheable(
            cacheNames = "cache-name123", key = "'two' + #param1",
            redis = @Redis(useRedisTemplate = "redisTemplate", expireTime = 20),
            caffeine = @Caffeine(expireTime = 40, maximumSize = 3)
    )
    public Object two(String param1) {
        System.err.println("进two了\t" + param1);
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
    
    @GetMapping("/two2")
    @ExtCacheable(cacheNames = "cache-name456", key = "'two' + #param1",
            redis = @Redis(useRedisTemplate = "stringRedisTemplate", expireTime = 20),
            caffeine = @Caffeine(expireTime = 20, maximumSize = 1))
    
    public Object two2(String param1) {
        System.err.println("进two2了\t" + param1);
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
}
