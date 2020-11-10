package com.niantou.springcacheext.cache.redis_caffeine;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.caffeine.ExtCaffeineCacheManager;
import com.niantou.springcacheext.cache.redis.ExtRedisCacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ext-redis-caffeine 缓存管理器
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 20:30:21
 */
public class ExtRedisCaffeineCacheManager implements CacheManager {
    
    /**
     * 是否以caffeine作为一级缓存.
     *     true - caffeine作为一级缓存,redis作为二级缓存
     *     false - redis作为一级缓存,caffeine作为二级缓存
     */
    
    private final boolean caffeineAsFirstCache;
    
    /**
     * (若一级缓存没数据，二级缓存有数据), 是否回填二级缓存的数据至一级缓存
     */
    private final boolean valueBackFill;
    
    private final ExtRedisCacheManager extRedisCacheManager;
    
    private final ExtCaffeineCacheManager extCaffeineCacheManager;
    
    public ExtRedisCaffeineCacheManager(ExtRedisCacheManager extRedisCacheManager,
                                        ExtCaffeineCacheManager extCaffeineCacheManager,
                                        boolean caffeineAsFirstCache, boolean valueBackFill) {
        this.extRedisCacheManager = extRedisCacheManager;
        this.extCaffeineCacheManager = extCaffeineCacheManager;
        this.caffeineAsFirstCache = caffeineAsFirstCache;
        this.valueBackFill = valueBackFill;
    }
    
    @Override
    public Cache getCache(@NonNull String name) {
        Cache firstCache;
        Cache secondCache;
        if (caffeineAsFirstCache) {
            firstCache = extCaffeineCacheManager.getCache(name);
            secondCache = extRedisCacheManager.getCache(name);
        } else {
            firstCache = extRedisCacheManager.getCache(name);
            secondCache = extCaffeineCacheManager.getCache(name);
        }
        return new ExtRedisCaffeineCache(firstCache, secondCache, valueBackFill);
    }
    
    @NonNull
    @Override
    public Collection<String> getCacheNames() {
        Set<String> cacheNameSet = new HashSet<>();
        Collection<String> redisCaches = extRedisCacheManager == null ? Collections.emptyList() : extRedisCacheManager.getCacheNames();
        Collection<String> caffeineCaches = extCaffeineCacheManager == null ? Collections.emptyList() : extCaffeineCacheManager.getCacheNames();
        cacheNameSet.addAll(redisCaches);
        cacheNameSet.addAll(caffeineCaches);
        return cacheNameSet;
    }
}