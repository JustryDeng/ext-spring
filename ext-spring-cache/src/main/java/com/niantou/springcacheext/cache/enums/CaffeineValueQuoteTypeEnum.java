package com.niantou.springcacheext.cache.enums;

import com.niantou.springcacheext.author.JustryDeng;

/**
 * caffeine中value的引用类型
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 14:46:39
 */
@SuppressWarnings("unused")
public enum CaffeineValueQuoteTypeEnum {
    
    /** 默认 */
    DEFAULT,
    
    /** 打开弱引用 */
    WEAK,
    
    /** 打开软引用 */
    SOFT
}
