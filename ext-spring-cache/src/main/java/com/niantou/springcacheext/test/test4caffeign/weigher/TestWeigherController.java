package com.niantou.springcacheext.test.test4caffeign.weigher;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.Caffeine;
import com.niantou.springcacheext.cache.annotation.ExtCacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试 {@link Caffeine#maximumWeight()}
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 13:29:03
 */
//@RestController
@SuppressWarnings("all")
@RequestMapping("/caffeine")
public class TestWeigherController {
    
    
    @GetMapping("/test-weigher")
    @ExtCacheable(cacheNames = "cache-name123", key = "#param1",
            caffeine = @Caffeine(expireTime = 600, maximumWeight = 3, weigher4MaximumWeight = "myWeigher"))
    public Object testWeigher(String param1) {
        System.err.println("进Test1Controller#testWeigher了\t" + param1);
        return param1 + ThreadLocalRandom.current().nextInt(100);
    }
    
}
