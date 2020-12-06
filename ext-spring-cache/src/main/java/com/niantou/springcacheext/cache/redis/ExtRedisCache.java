package com.niantou.springcacheext.cache.redis;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.annotation.RedisOop;
import com.niantou.springcacheext.cache.enums.RedisExpireStrategyEnum;
import com.niantou.springcacheext.cache.support.ExtCacheHelper;
import com.niantou.springcacheext.cache.support.SafeContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Map;

import static com.niantou.springcacheext.cache.support.ExtCacheHelper.LOG_PREFIX;

/**
 * 拓展RedisCache
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 21:33:03
 */
@Slf4j
public class ExtRedisCache extends RedisCache {
    
    private final String name;
    
    private final RedisCacheWriter cacheWriter;
    
    private final RedisCacheConfiguration cacheConfig;
    
    private final  Map<String, RedisCacheConfiguration> cacheConfigMap;
    
    /**
     * Create new {@link ExtRedisCache}.
     *
     * @param name
     *         must not be {@literal null}.
     * @param cacheWriter
     *         must not be {@literal null}.
     * @param cacheConfig
     *         must not be {@literal null}.
     * @param cacheConfigMap
     *         must not be {@literal null}.
     */
    protected ExtRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig,
                            Map<String, RedisCacheConfiguration> cacheConfigMap) {
        super(name, cacheWriter, cacheConfig);
        this.name = name;
        this.cacheWriter = cacheWriter;
        this.cacheConfig = cacheConfig;
        this.cacheConfigMap = cacheConfigMap;
    }
    
    @Override
    public ValueWrapper get(Object key) {
        Boolean refreshCurrCache = SafeContainer.THREAD_LOCAL_REFRESH_CURR_CACHE.get();
        if (BooleanUtils.isTrue(refreshCurrCache)) {
            // 这里返回null, 那么程序就会走方法里面的逻辑， 走完后再把结果放入缓存， 继而：变相实现了刷新当前key的缓存
            log.info(" refresh curr key[{}]'s cache-value", key);
            return null;
        }
        ValueWrapper valueWrapper = super.get(key);
        if (valueWrapper != null) {
            log.info(" got non-null valueWrapper from redis-cache, by key[{}]", key);
        }
        return valueWrapper;
    }
    
    @Override
    public void put(@NonNull Object key, @Nullable Object value) {
        Object cacheValue = preProcessCacheValue(value);
        if (!isAllowNullValues() && cacheValue == null) {
            throw new IllegalArgumentException(String.format(
                    LOG_PREFIX + "Cache '%s' does not allow 'null' values. Avoid storing null via '@Cacheable(unless=\"#result == null\")' or configure RedisCache to allow 'null' via RedisCacheConfiguration.",
                    name));
        }
        Duration ttl = cacheConfig.getTtl();
        Duration overrideTtl = overrideTtlIfNecessary(name);
        if (overrideTtl != null) {
            ttl = overrideTtl;
        }
        //noinspection ConstantConditions
        cacheWriter.put(name, createAndConvertCacheKey(key), serializeCacheValue(cacheValue), ttl);
    }
    
    @Override
    public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
        Object cacheValue = preProcessCacheValue(value);
        if (!isAllowNullValues() && cacheValue == null) {
            return get(key);
        }
        Duration ttl = cacheConfig.getTtl();
        Duration overrideTtl = overrideTtlIfNecessary(name);
        if (overrideTtl != null) {
            ttl = overrideTtl;
        }
        //noinspection ConstantConditions
        byte[] result = cacheWriter.putIfAbsent(name, createAndConvertCacheKey(key), serializeCacheValue(cacheValue), ttl);
        if (result == null) {
            return null;
        }
        return new SimpleValueWrapper(fromStoreValue(deserializeCacheValue(result)));
    }
    
    /**
     * 是否使用自定义的ttl。 若不使用，则返回null, 若使用，则返回要使用的ttl
     *
     * @param cacheName
     *            缓存的命名空间
     * @return null-不使用自定义的ttl; 否者使用自定义的ttl
     */
    private Duration overrideTtlIfNecessary(String cacheName) {
        ExtCacheableOop extCacheableOop = ExtCacheHelper.determineExtCacheOop(cacheName);
        if (extCacheableOop == null) {
            return null;
        }
        RedisOop redis = extCacheableOop.getRedis();
        RedisExpireStrategyEnum strategy = redis.getExpireStrategy();
        Duration customTtl = Duration.of(redis.getExpireTime(), redis.getTimeUnit());
        if (strategy == RedisExpireStrategyEnum.CUSTOM) {
            return customTtl;
        }
        RedisCacheConfiguration propertiesConfig = cacheConfigMap.get(cacheName);
        if (propertiesConfig == null) {
            return customTtl;
        }
        Duration ttl = propertiesConfig.getTtl();
        //noinspection ConstantConditions 配置的过期时间必须大于0才会生效
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            return customTtl;
        }
        // 返回null, 那么(上层逻辑会判断，并)会走配置文件中配置的Duration
        return null;
    }
    
    /**
     * see super#createAndConvertCacheKey
     */
    private byte[] createAndConvertCacheKey(Object key) {
        return serializeCacheKey(createCacheKey(key));
    }
}
