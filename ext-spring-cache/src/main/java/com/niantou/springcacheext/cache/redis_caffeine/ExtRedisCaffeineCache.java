package com.niantou.springcacheext.cache.redis_caffeine;

import com.niantou.springcacheext.author.JustryDeng;
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
    private final Cache firstCache;
    
    @Nullable
    private final Cache secondCache;
    
    /**
     * (若一级缓存没数据，二级缓存有数据), 是否回填二级缓存的数据至一级缓存
     */
    private final boolean valueBackFill;
    
    protected ExtRedisCaffeineCache(Cache firstCache, Cache secondCache, boolean valueBackFill) {
        Assert.notNull(firstCache, "firstCache cannot be null");
        Assert.notNull(secondCache, "secondCache cannot be null");
        this.firstCache = firstCache;
        this.secondCache = secondCache;
        this.valueBackFill = valueBackFill;
    }
    
    @Override
    public void put(Object key, Object value) {
        if (firstCache != null) {
            firstCache.put(key, value);
        }
        if (secondCache != null) {
            secondCache.put(key, value);
        }
    }
    
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper valueWrapper = null;
        if (firstCache != null) {
            valueWrapper = firstCache.putIfAbsent(key, value);
        }
        if (secondCache != null) {
            valueWrapper = secondCache.putIfAbsent(key, value);
        }
        return valueWrapper;
    }
    
    @Override
    public void evict(Object key) {
        if (firstCache != null) {
            firstCache.evict(key);
        }
        if (secondCache != null) {
            secondCache.evict(key);
        }
    }
    
    @Override
    public boolean evictIfPresent(Object key) {
        boolean redisResult = firstCache != null && firstCache.evictIfPresent(key);
        boolean caffeineResult = secondCache != null && secondCache.evictIfPresent(key);
        return redisResult && caffeineResult;
    }
    
    @Override
    public void clear() {
        if (firstCache != null) {
            firstCache.clear();
        }
        if (secondCache != null) {
            secondCache.clear();
        }
    }
    
    @Override
    public boolean invalidate() {
        boolean redisResult = firstCache != null && firstCache.invalidate();
        boolean caffeineResult = secondCache != null && secondCache.invalidate();
        return redisResult && caffeineResult;
    }
    
    @Override
    public String getName() {
        String name = null;
        
        if (firstCache != null) {
            name = firstCache.getName();
        }
        if (StringUtils.isEmpty(name) && secondCache != null) {
            name = secondCache.getName();
        }
        return name;
    }
    
    @Override
    public Object getNativeCache() {
        Object nativeCache = null;
        if (firstCache != null) {
            nativeCache = firstCache.getNativeCache();
        }
        if (Objects.isNull(nativeCache) && secondCache != null) {
            nativeCache = secondCache.getNativeCache();
        }
        return nativeCache;
    }
    
    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = null;
        if (firstCache != null) {
            valueWrapper = firstCache.get(key);
        }
        if (valueWrapper == null && secondCache != null) {
            valueWrapper = secondCache.get(key);
            // 二级缓存的数据回填至一级缓存
            if (valueBackFill && valueWrapper != null && firstCache != null) {
                firstCache.put(key, valueWrapper.get());
            }
        }
        return valueWrapper;
    }
    
    @Override
    public <T> T get(Object key, Class<T> type) {
        T t = null;
        if (firstCache != null) {
            t = firstCache.get(key, type);
        }
        if (Objects.isNull(t) && secondCache != null) {
            t = secondCache.get(key, type);
        }
        return t;
    }
    
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        @SuppressWarnings("DuplicatedCode") T t = null;
        if (firstCache != null) {
            t = firstCache.get(key, valueLoader);
        }
        if (Objects.isNull(t) && secondCache != null) {
            t = secondCache.get(key, valueLoader);
        }
        return t;
    }
}
