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
     * <p>
     * 注:过期时间必须与cacheName一起配置才会生效。即:只配置了过期时间，但是没有指定任何cacheName(或指定的cacheName不对)时，也是不会走配置的过期时间的。
     */
    AUTO,
    
    /**
     * 走注解中指定的过期时间
     */
    CUSTOM
}
