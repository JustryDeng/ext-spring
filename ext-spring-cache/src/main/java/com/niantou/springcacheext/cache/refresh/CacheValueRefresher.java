package com.niantou.springcacheext.cache.refresh;

import com.niantou.springcacheext.author.JustryDeng;
import org.aspectj.lang.JoinPoint;

/**
 * 缓存值刷新器
 *
 * @author {@link JustryDeng}
 * @since 2020/11/23 12:27:09
 */
public interface CacheValueRefresher {
    
    /**
     * 是否应该刷新缓存值
     *
     * @param joinPoint
     *            切面获取到的JoinPoint
     * @return  是否应该刷新缓存值
     */
    boolean refresh(JoinPoint joinPoint);
}
