package com.niantou.springcacheext.cache.redis_caffeine;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.caffeine.ExtCaffeineCache;
import com.niantou.springcacheext.cache.redis.ExtRedisCache;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * 简单组合ExtRedisCache与ExtCaffeineCache
 * <p>
 * 注意，在简易实现下， 对应的实现类可能仅仅支持一些常用的场景， 对某些复杂或者偏冷的场景，支持性不太好
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 21:33:03
 */
@Slf4j
@Setter
@Getter
@ToString
@SuppressWarnings("NullableProblems")
public class ExtRedisCaffeineCache implements Cache {
    
    @Nullable
    private final ExtRedisCache extRedisCache;
    
    @Nullable
    private final ExtCaffeineCache extCaffeineCache;
    
    protected ExtRedisCaffeineCache(ExtRedisCache extRedisCache, ExtCaffeineCache extCaffeineCache) {
        Assert.notNull(extRedisCache, "extRedisCache cannot be null");
        Assert.notNull(extCaffeineCache, "extCaffeineCache cannot be null");
        this.extRedisCache = extRedisCache;
        this.extCaffeineCache = extCaffeineCache;
    }
    
    @Override
    public void put(Object key, Object value) {
        // 放redis
        if (extRedisCache != null) {
            extRedisCache.put(key, value);
        }
        // 放caffeine
        if (extCaffeineCache != null) {
            extCaffeineCache.put(key, value);
        }
    }
    
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        // 放redis
        ValueWrapper valueWrapper = null;
        if (extRedisCache != null) {
            valueWrapper = extRedisCache.putIfAbsent(key, value);
        }
        // 放caffeine
        if (extCaffeineCache != null) {
            valueWrapper = extCaffeineCache.putIfAbsent(key, value);
        }
        return valueWrapper;
    }
    
    @Override
    public void evict(Object key) {
        if (extRedisCache != null) {
            extRedisCache.evict(key);
        }
        if (extCaffeineCache != null) {
            extCaffeineCache.evict(key);
        }
    }
    
    @Override
    public boolean evictIfPresent(Object key) {
        boolean redisResult = extRedisCache != null && extRedisCache.evictIfPresent(key);
        boolean caffeineResult = extCaffeineCache != null && extCaffeineCache.evictIfPresent(key);
        return redisResult && caffeineResult;
    }
    
    @Override
    public void clear() {
        if (extRedisCache != null) {
            extRedisCache.clear();
        }
        if (extCaffeineCache != null) {
            extCaffeineCache.clear();
        }
    }
    
    @Override
    public boolean invalidate() {
        boolean redisResult = extRedisCache != null && extRedisCache.invalidate();
        boolean caffeineResult = extCaffeineCache != null && extCaffeineCache.invalidate();
        return redisResult && caffeineResult;
    }
    
    @Override
    public String getName() {
        String name = null;
        
        if (extRedisCache != null) {
            name = extRedisCache.getName();
        }
        if (StringUtils.isEmpty(name) && extCaffeineCache != null) {
            name = extCaffeineCache.getName();
        }
        return name;
    }
    
    @Override
    public Object getNativeCache() {
        Object nativeCache = null;
        if (extRedisCache != null) {
            nativeCache = extRedisCache.getNativeCache();
        }
        if (Objects.isNull(nativeCache) && extCaffeineCache != null) {
            nativeCache = extCaffeineCache.getNativeCache();
        }
        return nativeCache;
    }
    
    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = null;
        if (extRedisCache != null) {
            valueWrapper = extRedisCache.get(key);
        }
        if (valueWrapper == null && extCaffeineCache != null) {
            valueWrapper = extCaffeineCache.get(key);
        }
        return valueWrapper;
    }
    
    @Override
    public <T> T get(Object key, Class<T> type) {
        T t = null;
        if (extRedisCache != null) {
            t = extRedisCache.get(key, type);
        }
        if (Objects.isNull(t) && extCaffeineCache != null) {
            t = extCaffeineCache.get(key, type);
        }
        return t;
    }
    
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        @SuppressWarnings("DuplicatedCode") T t = null;
        if (extRedisCache != null) {
            t = extRedisCache.get(key, valueLoader);
        }
        if (Objects.isNull(t) && extCaffeineCache != null) {
            t = extCaffeineCache.get(key, valueLoader);
        }
        return t;
    }
}
