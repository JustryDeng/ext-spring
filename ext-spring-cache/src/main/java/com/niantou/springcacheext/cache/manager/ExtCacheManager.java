package com.niantou.springcacheext.cache.manager;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.enums.ExtCacheTypeEnum;
import com.niantou.springcacheext.cache.provider.CacheManagerProvider;
import com.niantou.springcacheext.cache.provider.ExtCaffeineCacheManagerProvider;
import com.niantou.springcacheext.cache.provider.ExtRedisCacheManagerProvider;
import com.niantou.springcacheext.cache.provider.ExtRedisCaffeineCacheManagerProvider;
import com.niantou.springcacheext.cache.support.ExtCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;

import static com.niantou.springcacheext.cache.constant.ExtCacheConfigPlaceholder.USE_DEFAULT_CACHE_MANAGER_IF_MISS;
import static com.niantou.springcacheext.cache.support.ExtCacheHelper.LOG_PREFIX;

/**
 * 自定义缓存管理器
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 12:56:08
 */
@SuppressWarnings({"NullableProblems"})
@Slf4j
public class ExtCacheManager implements CacheManager, SmartInitializingSingleton {
    
    public static final String BEAN_NAME = "extCacheManager";
    
    @Value(USE_DEFAULT_CACHE_MANAGER_IF_MISS)
    private boolean useDefaultCacheManagerIfMiss;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Resource(name = ExtRedisCacheManagerProvider.BEAN_NAME)
    private CacheManagerProvider extRedisCacheManagerProvider;
    
    @Resource(name = ExtCaffeineCacheManagerProvider.BEAN_NAME)
    private CacheManagerProvider extCaffeineCacheManagerProvider;
    
    @Resource(name = ExtRedisCaffeineCacheManagerProvider.BEAN_NAME)
    private CacheManagerProvider extRedisCaffeineCacheManagerProvider;
    
    private CacheManager defaultCacheManager = null;
    
    @Override
    public Cache getCache(String name) {
        ExtCacheableOop oop = ExtCacheHelper.determineExtCacheOop(name);
        // 获取对应的CacheManager
        CacheManager cacheManager = pivot(oop);
        // 如果为空的话， 是否使用默认的cacheManager
        if (cacheManager == null && useDefaultCacheManagerIfMiss) {
            cacheManager = defaultCacheManager;
        }
        if (cacheManager == null) {
            return null;
        }
        return cacheManager.getCache(name);
    }
    
    @Override
    public Collection<String> getCacheNames() {
        ExtCacheableOop oop = ExtCacheHelper.determineExtCacheOop();
        // 获取对应的CacheManager
        CacheManager cacheManager = pivot(oop);
        // 如果为空的话， 是否使用默认的cacheManager
        if (cacheManager == null && useDefaultCacheManagerIfMiss) {
            cacheManager = defaultCacheManager;
        }
        if (cacheManager == null) {
            return Collections.emptyList();
        }
        return cacheManager.getCacheNames();
    }
    
    @Override
    public void afterSingletonsInstantiated() {
        // 初始化defaultCacheManager
        CacheManager cacheManager = applicationContext.getBean(CacheManager.class);
        if (cacheManager instanceof ExtCacheManager) {
            log.warn(LOG_PREFIX + " ExtCacheManager cannot be as a default CacheManager");
            return;
        }
        log.info(LOG_PREFIX + " found defaultCacheManager [{}]", cacheManager);
        defaultCacheManager = cacheManager;
    }
    
    /**
     * 根据当前注解信息对象，路由获取对应的CacheManager
     *
     * @param oop
     *            当前注解信息对象
     * @return  oop对应的CacheManager
     */
    private CacheManager pivot(ExtCacheableOop oop) {
        CacheManager cacheManager;
        ExtCacheTypeEnum extCacheTypeEnum = ExtCacheTypeEnum.parseCacheType(oop);
        switch (extCacheTypeEnum) {
            case REDIS:
                cacheManager = extRedisCacheManagerProvider.provide(oop);
                break;
            case CAFFEINE:
                cacheManager = extCaffeineCacheManagerProvider.provide(oop);
                break;
            case REDIS_CAFFEINE:
                cacheManager = extRedisCaffeineCacheManagerProvider.provide(oop);
                break;
            default:
                throw new IllegalArgumentException(LOG_PREFIX + "cannot support switch for enum [" + extCacheTypeEnum + "], \n\t curr ExtCacheableOop is -> " + oop);
        }
        return cacheManager;
    }
}