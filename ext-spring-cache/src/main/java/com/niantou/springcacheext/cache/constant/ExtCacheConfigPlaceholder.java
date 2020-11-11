package com.niantou.springcacheext.cache.constant;

import com.niantou.springcacheext.author.JustryDeng;

/**
 * spring配置占位符
 *
 * @author {@link JustryDeng}
 * @since 2020/11/10 3:11:54
 */
public abstract class ExtCacheConfigPlaceholder {
    
    /** 控制是否打印ext-spring-cache banner */
    public static final String PRINT_BANNER_PLACEHOLDER = "${ext.spring.cache.print-banner:true}";
    
    /** 当没有ext管理器可以被总管理器ExtCacheManager管理时，管理器ExtCacheManager是否管理默认的CacheManager */
    public static final String USE_DEFAULT_CACHE_MANAGER_IF_MISS = "${ext.spring.cache.use-default-cache-manager-if-miss:true}";
    
    /**
     * true:
     *      默认对spring-context中的配置作出响应。  即：在spring-context中若存在相应配置或相关bean，那么会影响所有的Redis。
     *      此时，可通过在使用@Redis注解时，显示的指定相关配置来覆盖 spring-context中的配置
     *
     * false:
     *      默认不对spring-context中的配置作出响应。即：不管spring-context是否存在相应配置或相关bean，都不会影响Redis。
     *      此时，在使用@Redis注解时，显示的指定相关配置依然有效
     */
    public static final String REDIS_RESPONSE_SPRING_CONTEXT_PLACEHOLDER = "${ext.spring.cache.redis.response-spring-context:true}";
    
    /**
     * true:
     *      默认对spring-context中的配置作出响应。  即：在spring-context中若存在相应配置或相关bean，那么会影响所有的Caffeine。
     *      此时，可通过在使用@Caffeine注解时，显示的指定相关配置来覆盖 spring-context中的配置
     *
     * false:
     *      默认不对spring-context中的配置作出响应。即：不管spring-context是否存在相应配置或相关bean，都不会影响Caffeine。
     *      此时，在使用@Caffeine注解时，显示的指定相关配置依然有效
     */
    public static final String CAFFEINE_RESPONSE_SPRING_CONTEXT_PLACEHOLDER = "${ext.spring.cache.caffeine.response-spring-context:true}";
    
    /**
     * true:
     *      默认对spring-context中的配置作出响应。  即：在spring-context中若存在相应配置或相关bean，那么会影响所有的Redis、Caffeine。
     *      此时，可通过在使用@Redis、@Caffeine注解时，显示的指定相关配置来覆盖 spring-context中的配置
     *
     * false:
     *      默认不对spring-context中的配置作出响应。即：不管spring-context是否存在相应配置或相关bean，都不会影响Redis、Caffeine。
     *      此时，在使用@Redis、@Caffeine注解时，显示的指定相关配置依然有效
     */
    public static final String REDIS_CAFFEINE_RESPONSE_SPRING_CONTEXT_PLACEHOLDER = "${ext.spring.cache.redis-caffeine.response-spring-context:true}";
    
    /**
     * 是否以caffeine作为一级缓存
     *     true - caffeine作为一级缓存,redis作为二级缓存
     *     false - redis作为一级缓存,caffeine作为二级缓存
     */
    public static final String REDIS_CAFFEINE_CAFFEINE_AS_FIRST_CACHE = "${ext.spring.cache.redis-caffeine.caffeine-as-first-cache:true}";
    
    /**
     * (若一级缓存没数据，二级缓存有数据), 是否回填二级缓存的数据至一级缓存
     */
    public static final String REDIS_CAFFEINE_VALUE_BACK_FILL = "${ext.spring.cache.redis-caffeine.value-back-fill:true}";
    
}
