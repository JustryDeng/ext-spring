package com.niantou.springcacheext.test.test4caffeign.loader4refresh;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.Caffeine;
import com.niantou.springcacheext.cache.annotation.ExtCacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试 {@link Caffeine#refreshAfterWrite()}
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 13:29:03
 */
//@RestController
@RequestMapping("/caffeine")
@SuppressWarnings("all")
public class TestLoader4RefreshController {
    
    
    @GetMapping("/test-load-for-refresh")
    @ExtCacheable(cacheNames = "cache-name123", key = "#param1",
            caffeine = @Caffeine(expireTime = 600, maximumSize = 5, refreshAfterWrite = 10, cacheLoader4Refresh = "myCacheLoader"))
    public Object test(String param1) {
        System.err.println("进Test1Controller#testRefresh了\t" + param1);
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
    
}
