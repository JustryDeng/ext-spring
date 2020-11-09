package com.niantou.springcacheext.cache.annotation;

import com.niantou.springcacheext.author.JustryDeng;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;

/**
 * {@link ExtCacheable}中的信息的容器类
 *
 * P.S. 未作说明的字段， 其语意去ExtCacheable中见对应的字段说明即可
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 19:08:31
 */
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class ExtCacheableOop {

    /** 当前对象对应的ExtCacheable所处的类 */
    private final Class<?> clazz;
    
    /** 当前对象对应的ExtCacheable所处的方法 */
    private final Method method;
    
    /** 对应{@link ExtCacheable#redis}中的第一个元素, 如果{@link ExtCacheable#redis}不为空的话 */
    private final RedisOop redis;
    
    /** 对应{@link ExtCacheable#caffeine}中的第一个元素, 如果{@link ExtCacheable#caffeine}不为空的话 */
    private final CaffeineOop caffeine;
    
    private final String[] cacheNames;
    
    private final String key;
    
    private final String[] value;
    
    private final String keyGenerator;
    
    private final String cacheManager;
    
    private final String cacheResolver;
    
    private final String condition;
    
    private final String unless;
    
    private final boolean sync;
}
