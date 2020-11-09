package com.niantou.springcacheext.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.niantou.springcacheext.author.JustryDeng;
import org.springframework.cache.caffeine.CaffeineCache;

/**
 * ext CaffeineCache
 * <p>
 * P.S. 暂时空继承吧， 啥也不干
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 15:47:10
 */
@SuppressWarnings("unused")
public class ExtCaffeineCache extends CaffeineCache {
    
    public ExtCaffeineCache(String name, Cache<Object, Object> cache) {
        super(name, cache);
    }
    
    public ExtCaffeineCache(String name, Cache<Object, Object> cache, boolean allowNullValues) {
        super(name, cache, allowNullValues);
    }
    
}