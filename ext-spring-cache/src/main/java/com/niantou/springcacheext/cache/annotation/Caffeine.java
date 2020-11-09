package com.niantou.springcacheext.cache.annotation;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.enums.CaffeineExpireStrategyEnum;
import com.niantou.springcacheext.cache.enums.CaffeineKeyQuoteTypeEnum;
import com.niantou.springcacheext.cache.enums.CaffeineValueQuoteTypeEnum;

import java.time.temporal.ChronoUnit;

/**
 * Caffeine缓存相关信息
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 14:50:29
 */
public @interface Caffeine {
    
    /* --------------------------------------- 常用配置 --------------------------------------- */
    
    /**
     * caffeine缓存的初始化容量大小
     * <p>
     * 注: caffeine存数据时，其实就是用的一种自定义的Map子类实现存储的，所以初始化容量即为Map的初始化容量大小
     * 注: 所以设置initialCapacity时以2的n次幂最佳
     */
    int initialCapacity() default 128;
    
    /**
     * caffeine缓存条目(注:可理解为Map键值对)的个数上限
     * <p>
     * 注: 若 maximumSize > 0， 同时maximumWeight > 0, 那么默认选择走maximumSize
     * <p>
     * P.S. 超过的话，在刷新缓存时将根据策略定位并淘汰性价比低的键值对
     */
    long maximumSize() default -1;
    
    /** 当前键值对的过期时间 */
    int expireTime() default -1;
    
    /** {@link this#expireTime}的单位timeUnit */
    ChronoUnit timeUnit() default ChronoUnit.SECONDS;
    
    /** 缓存过期策略 */
    CaffeineExpireStrategyEnum expireStrategy() default CaffeineExpireStrategyEnum.EXPIRE_AFTER_WRITE;
    
    
    /* --------------------------------------- 不常用配置 --------------------------------------- */
    
    
    /** 是否打开统计功能 */
    boolean recordStats() default false;
    
    /** 设置key对象的引用类型 */
    CaffeineKeyQuoteTypeEnum keyQuoteType() default CaffeineKeyQuoteTypeEnum.DEFAULT;
    
    /** 设置value对象的引用类型 */
    CaffeineValueQuoteTypeEnum valueQuoteType() default CaffeineValueQuoteTypeEnum.DEFAULT;
    
    /*
     * 何时启用maximumWeight?
     *       当maximumSize <= 0，同时maximumWeight > 0时。
     *
     * check: 当启用maximumWeight时，
     *        需要保证： 从容器中能获取到bean-name为${writer4MaximumWeight}的com.github.benmanes.caffeine.cache.Weigher
     */
    
    /**
     * caffeine缓存条目(注:可理解为Map键值对)的权重上限。
     * <p>
     * 注: 若 maximumSize > 0， 同时maximumWeight > 0, 那么默认选择走maximumSize
     * <p>
     * P.S. 超过的话，在刷新缓存时会将“重量”超过此值的键值对淘汰
     * @see this#weigher4MaximumWeight
     */
    long maximumWeight() default -1;
    
    /**
     * key-value的称重器, "重量"超过maximumWeight的将被淘汰
     * <p>
     * 值应该填{@link com.github.benmanes.caffeine.cache.Weigher}的spring-bean name
     */
    String weigher4MaximumWeight() default "";
    
    /*
     * 何时启用refreshAfterWrite?
     *       当refreshAfterWrite >0 时。
     *
     * check: 当启用refreshAfterWrite时，
     *        需要保证： 从容器中能获取到bean-name为${cacheLoader4Refresh}的com.github.benmanes.caffeine.cache.CacheLoader实例
     */
    
    /**
     * 刷新缓存的时间间隔
     *
     * 若<code>refreshAfterWrite <= 0</code>，则表示不刷新
     */
    int refreshAfterWrite() default -1;
    
    /** {@link this#refreshAfterWrite}的单位 */
    ChronoUnit timeUnit4Refresh() default ChronoUnit.SECONDS;
    
    /**
     * 缓存加载器, 在刷新缓存时，需要用到缓存加载器
     * <p>
     * 值应该填{@link com.github.benmanes.caffeine.cache.CacheLoader}的spring-bean name
     */
    String cacheLoader4Refresh() default "";
}