package com.niantou.springcacheext.cache.constant;

import com.niantou.springcacheext.author.JustryDeng;

/**
 * spring配置占位符
 *
 * @author {@link JustryDeng}
 * @since 2020/11/10 3:11:54
 */
public abstract class ExtCacheConfigPlaceholderBase {
    
    /** 控制是否打印ext-spring-cache banner */
    public static final String PRINT_BANNER_PLACEHOLDER = "${ext.spring.cache.print-banner:true}";
    
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
    
}
