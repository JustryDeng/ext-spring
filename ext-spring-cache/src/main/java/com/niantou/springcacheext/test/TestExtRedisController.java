package com.niantou.springcacheext.test;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.Caffeine;
import com.niantou.springcacheext.cache.annotation.ExtCacheable;
import com.niantou.springcacheext.cache.annotation.Redis;
import com.niantou.springcacheext.cache.enums.RedisExpireStrategyEnum;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试 ext-redis
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 13:29:03
 */
@RestController
@RequestMapping("/redis")
@SuppressWarnings("SpringCacheNamesInspection")
public class TestExtRedisController {
    
    @GetMapping("/one")
    @Cacheable(cacheNames = "JustryDeng", key = "'one' + #param1")
    public Object one(String param1) {
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
    
    @GetMapping("/two")
    @ExtCacheable(cacheNames = "JustryDeng", key = "'two' + #param1",
                  redis = @Redis(useRedisTemplate = "redisTemplate",
                                 expireTime = 100,
                                 timeUnit = ChronoUnit.MINUTES,
                                 expireStrategy = RedisExpireStrategyEnum.AUTO
                  )
    )
    public Object two(String param1) {
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
    
    @GetMapping("/two2")
    @ExtCacheable(cacheNames = "JustryDeng", key = "'two2' + #param1", redis = @Redis(useRedisTemplate = "redisTemplate",
            expireTime = 100, expireStrategy = RedisExpireStrategyEnum.CUSTOM))
    public Object two2(String param1) {
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
}
