package com.niantou.springcacheext.test;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.Caffeine;
import com.niantou.springcacheext.cache.annotation.ExtCacheable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

import static com.niantou.springcacheext.cache.enums.CaffeineExpireStrategyEnum.EXPIRE_AFTER_ACCESS;

/**
 * 测试 ext-caffeine
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 13:29:03
 */
@RestController
@RequestMapping("/caffeine")
@SuppressWarnings("SpringCacheNamesInspection")
public class TestExtCaffeineController {
    
    @GetMapping("/one")
    @Cacheable(cacheNames = "cache-name123", key = "'one' + #param1")
    public Object one(String param1) {
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
    
    @GetMapping("/two")
    @ExtCacheable(
            cacheNames = "cache-name123", key = "'two' + #param1",
            caffeine = @Caffeine(expireTime = 300, maximumSize = 3, expireStrategy = EXPIRE_AFTER_ACCESS)
    )
    public Object two(String param1) {
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
    
    @GetMapping("/two2")
    @ExtCacheable(cacheNames = "cache-name456", key = "'two' + #param1", caffeine = @Caffeine(expireTime = 300, maximumSize = 1))
    public Object two2(String param1) {
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
}
