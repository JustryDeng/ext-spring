package com.niantou.springcacheext.cache.enums;

import com.niantou.springcacheext.author.JustryDeng;

/**
 * 缓存时间策略
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 14:46:39
 */
@SuppressWarnings("unused")
public enum CaffeineExpireStrategyEnum {
    
    /**
     * 最后一次写入(或访问)后经过固定时间过期
     */
    EXPIRE_AFTER_ACCESS,
    
    /**
     * 最后一次写入后经过固定时间过期
     */
    EXPIRE_AFTER_WRITE
}
