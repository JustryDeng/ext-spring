package com.niantou.springcacheext.test.test4caffeign.weigher;

import com.github.benmanes.caffeine.cache.Weigher;
import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.Caffeine;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.stereotype.Component;

/**
 * 测试 {@link Caffeine#maximumWeight()}
 *
 * @author {@link JustryDeng}
 * @since 2020/11/9 20:57:12
 */
//@Component
public class MyWeigher implements Weigher {
    
    @Override
    public @NonNegative int weigh( Object key, Object value) {
        return String.valueOf(key).length();
    }
    
}
