package com.niantou.springcacheext.cache.enums;

import com.niantou.springcacheext.author.JustryDeng;

/**
 * 缓存时间策略
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 14:46:39
 */
@SuppressWarnings("unused")
public enum RedisExpireStrategyEnum {
    
    /**
     * 若配置文件中配置有过期时间，那么走配置文件中配置的的过期时间；若配置文件中没有配置过期时间，那么走注解中指定的过期时间
     */
    AUTO,
    
    /**
     * 走注解中指定的过期时间
     */
    CUSTOM
}
