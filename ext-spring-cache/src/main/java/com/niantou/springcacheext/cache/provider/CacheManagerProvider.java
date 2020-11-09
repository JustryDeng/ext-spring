package com.niantou.springcacheext.cache.provider;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.CacheManager;

/**
 * 提供CacheManager
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 12:19:58
 */
public interface CacheManagerProvider extends SmartInitializingSingleton {
    
    /**
     * 获取对应的缓存管理器
     *
     * @param oop
     *            缓存的相关信息
     * @return  对应的缓存换管理
     */
    CacheManager provide(ExtCacheableOop oop);
}
