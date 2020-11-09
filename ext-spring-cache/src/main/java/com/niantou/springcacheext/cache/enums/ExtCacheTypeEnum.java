package com.niantou.springcacheext.cache.enums;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;

import java.util.Objects;

import static com.niantou.springcacheext.cache.support.ExtCacheHelper.LOG_PREFIX;

/**
 * 缓存类型枚举
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 14:46:39
 */
public enum ExtCacheTypeEnum {
    
    /** redis缓存 */
    REDIS,
    
    /** 本地caffeine缓存 */
    CAFFEINE,
    
    /** redis + caffeine缓存 */
    REDIS_CAFFEINE;
    
    /**
     * 解析extCacheableOop对应的缓存类型
     *
     * @param extCacheableOop
     *            缓存信息模型
     * @return  缓存类型
     */
    public static ExtCacheTypeEnum parseCacheType(ExtCacheableOop extCacheableOop) {
        Objects.requireNonNull(extCacheableOop, LOG_PREFIX + " extCacheableOop cannot be null");
        boolean redisNonNull = extCacheableOop.getRedis() != null;
        boolean caffeineNonNull = extCacheableOop.getCaffeine() != null;
        if (redisNonNull && caffeineNonNull) {
            return REDIS_CAFFEINE;
        }
        if (redisNonNull) {
            return REDIS;
        }
        if (caffeineNonNull) {
            return CAFFEINE;
        }
        throw new IllegalArgumentException(LOG_PREFIX + " cannot parseCacheType for -> " + extCacheableOop);
    }
}
