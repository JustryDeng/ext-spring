package com.niantou.springcacheext.cache.caffeine;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.support.ExtCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.niantou.springcacheext.cache.support.ExtCacheHelper.LOG_PREFIX;

/**
 * ext-caffeine 缓存管理器
 * <p>
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 14:46:28
 */
public class ExtCaffeineCacheManager extends CaffeineCacheManager {
    
    private boolean dynamic = true;
    
    private final Map<ExtCacheableOop, Cache> customCacheMap = new ConcurrentHashMap<>(16);
    
    /**
     * 创建Cache时，{@link CaffeineCacheManager#createCaffeineCache(String)} 会调用此方法的，
     * 重写了此方法就相当于重写了createCaffeineCache。
     * <p>
     * TIPS. 低版本的spring-boot里面，是没有此方法的，那时就需要重写{@link CaffeineCacheManager#createCaffeineCache(String)}了
     */
    @NonNull
    @Override
    protected Cache adaptCaffeineCache(@Nullable String name, @Nullable com.github.benmanes.caffeine.cache.Cache<Object, Object> cache) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(cache, "Cache must not be null");
        return new ExtCaffeineCache(name, cache, isAllowNullValues());
    }
    
    @Override
    public Cache getCache(@Nullable String name) {
        Assert.notNull(name, "Name must not be null");
        ExtCacheableOop oop = ExtCacheHelper.determineExtCacheOop(name);
        if (oop == null) {
            return this.dynamic ? createCaffeineCache(name) : null;
        }
        Cache cache = this.customCacheMap.get(oop);
        if (cache == null) {
            cache = this.dynamic ? createCaffeineCache(name) : null;
            if (cache != null) {
                this.customCacheMap.put(oop, cache);
            }
        }
        return cache;
    }
    
    @Override
    public void setCacheNames(@Nullable Collection<String> cacheNames) {
        this.dynamic = cacheNames == null;
        super.setCacheNames(cacheNames);
    }
    
    /**
     * 参考{@link org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration}进行改造
     */
    @Slf4j
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static class ExtCaffeineCacheManagerBuilder {
        
        private final Caffeine<Object, Object> caffeine;
        private final CacheLoader<Object, Object> cacheLoader;
        private final List<String> cacheNames;
        
        private boolean allowNullValues = true;
        private Caffeine<Object, Object> customCaffeine;
        private CacheLoader<Object, Object> customCacheLoader;
    
        private ExtCaffeineCacheManagerBuilder(CacheProperties cacheProperties, Caffeine<Object, Object> caffeine, CacheLoader<Object, Object> cacheLoader) {
            if (cacheProperties == null || CollectionUtils.isEmpty(cacheProperties.getCacheNames())) {
                cacheNames = new ArrayList<>(0);
            } else {
                cacheNames = cacheProperties.getCacheNames();
            }
            this.caffeine = caffeine;
            this.cacheLoader = cacheLoader;
        }
        
        public static ExtCaffeineCacheManagerBuilder builder(CacheProperties cacheProperties, ObjectProvider<Caffeine<Object, Object>> caffeineProvider,
                                                      ObjectProvider<CaffeineSpec> caffeineSpecProvider, ObjectProvider<CacheLoader<Object, Object>> cacheLoaderProvider) {
            Caffeine<Object, Object> caffeine = null;
            if (cacheProperties != null && cacheProperties.getCaffeine() != null && StringUtils.hasText(cacheProperties.getCaffeine().getSpec())) {
                caffeine = Caffeine.from(cacheProperties.getCaffeine().getSpec());
            } else if (caffeineSpecProvider.getIfAvailable() != null) {
                caffeine = Caffeine.from(caffeineSpecProvider.getIfAvailable());
            } else if (caffeineProvider.getIfAvailable() != null) {
                caffeine = caffeineProvider.getIfAvailable();
            } else {
                log.trace(LOG_PREFIX + " cannot found any spring-env-config for caffeine-cache");
            }
            return new ExtCaffeineCacheManagerBuilder(cacheProperties, caffeine, cacheLoaderProvider.getIfAvailable());
        }
        
        public ExtCaffeineCacheManager build() {
            ExtCaffeineCacheManager cacheManager = new ExtCaffeineCacheManager();
            cacheManager.setCaffeine(customCaffeine == null ? caffeine : customCaffeine);
            cacheManager.setCacheLoader(customCacheLoader == null ? cacheLoader : customCacheLoader);
            cacheManager.setAllowNullValues(allowNullValues);
            if (!CollectionUtils.isEmpty(cacheNames)) {
                cacheManager.setCacheNames(cacheNames);
            }
            return cacheManager;
        }
    
        /**
         * 定制 命名空间
         */
        public ExtCaffeineCacheManagerBuilder customAppendCacheNames(Set<String> cacheNameSet, boolean cleanExisted) {
            if (cleanExisted) {
                cacheNames.clear();
            }
            cacheNames.addAll(cacheNameSet);
            return this;
        }
        
        /**
         * 定制 allowNullValues
         */
        public ExtCaffeineCacheManagerBuilder custom(boolean allowNullValues) {
            this.allowNullValues = allowNullValues;
            return this;
        }
        
        /**
         * 定制 Caffeine<Object, Object>
         */
        public ExtCaffeineCacheManagerBuilder custom(Caffeine<Object, Object> caffeine) {
            this.customCaffeine = caffeine;
            return this;
        }
    
        /**
         * 定制CacheLoader<Object, Object>
         */
        public ExtCaffeineCacheManagerBuilder custom(CacheLoader<Object, Object> cacheLoader) {
            this.customCacheLoader = cacheLoader;
            return this;
        }
    }
}
