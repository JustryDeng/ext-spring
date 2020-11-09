package com.niantou.springcacheext.test.test4caffeign.loader4refresh;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.Caffeine;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 测试 {@link Caffeine#refreshAfterWrite()}
 *
 * @author {@link JustryDeng}
 * @since 2020/11/9 21:23:23
 */
//@Component
public class MyCacheLoader implements CacheLoader {

    @Override
    public Object load(Object key) throws Exception {
        LocalTime localTime = LocalTime.now();
        return key + " - obj - " + localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
