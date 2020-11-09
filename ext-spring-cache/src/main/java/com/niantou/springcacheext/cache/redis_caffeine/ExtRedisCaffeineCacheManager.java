package com.niantou.springcacheext.cache.redis_caffeine;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.caffeine.ExtCaffeineCache;
import com.niantou.springcacheext.cache.caffeine.ExtCaffeineCacheManager;
import com.niantou.springcacheext.cache.redis.ExtRedisCache;
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
    
    private final ExtRedisCacheManager extRedisCacheManager;
    
    private final ExtCaffeineCacheManager extCaffeineCacheManager;
    
    public ExtRedisCaffeineCacheManager(ExtRedisCacheManager extRedisCacheManager,
                                        ExtCaffeineCacheManager extCaffeineCacheManager) {
        this.extRedisCacheManager = extRedisCacheManager;
        this.extCaffeineCacheManager = extCaffeineCacheManager;
    }
    
    
    @Override
    public Cache getCache(@NonNull String name) {
        Cache redisCache = extRedisCacheManager.getCache(name);
        Cache caffeineCache = extCaffeineCacheManager.getCache(name);
        return new ExtRedisCaffeineCache((ExtRedisCache)redisCache, (ExtCaffeineCache)caffeineCache);
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