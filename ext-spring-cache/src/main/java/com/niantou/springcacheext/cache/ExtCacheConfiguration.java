package com.niantou.springcacheext.cache;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.caffeine.ExtCaffeineCacheInitParser;
import com.niantou.springcacheext.cache.event.EnableExtSpringCacheEvent;
import com.niantou.springcacheext.cache.event.listener.EnableExtSpringCacheListener;
import com.niantou.springcacheext.cache.manager.ExtCacheManager;
import com.niantou.springcacheext.cache.parser.ExtCacheChecker;
import com.niantou.springcacheext.cache.provider.ExtCaffeineCacheManagerProvider;
import com.niantou.springcacheext.cache.provider.ExtRedisCacheManagerProvider;
import com.niantou.springcacheext.cache.provider.ExtRedisCaffeineCacheManagerProvider;
import com.niantou.springcacheext.cache.redis.ExtRedisCacheInitParser;
import com.niantou.springcacheext.cache.redis_caffeine.ExtRedisCaffeineCacheInitParser;
import com.niantou.springcacheext.cache.support.ExtCacheAroundHandlerAdvice;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * config for ext-cache
 * <p>
 * 我们希望： @ExtCacheable 走 ExtCacheManager， 而原来的@Cacheable，任然走原来默认的CacheManager，
 *           所以这里@AutoConfigureAfter(CacheAutoConfiguration.class)
 *           P.S. 默认的cacheManager是哪个可见{@link org.springframework.cache.interceptor.CacheAspectSupport#afterSingletonsInstantiated()}
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 12:56:08
 */
@AutoConfigureAfter(CacheAutoConfiguration.class)
public class ExtCacheConfiguration implements ApplicationContextAware {
    
    @Bean(ExtRedisCaffeineCacheManagerProvider.BEAN_NAME)
    public ExtRedisCaffeineCacheManagerProvider extRedisCaffeineCacheManagerProvider() {
        return new ExtRedisCaffeineCacheManagerProvider();
    }
    
    @Bean(ExtRedisCaffeineCacheInitParser.BEAN_NAME)
    public ExtRedisCaffeineCacheInitParser extRedisCaffeineCacheInitParser() {
        return new ExtRedisCaffeineCacheInitParser();
    }
    
    @Bean(ExtCaffeineCacheManagerProvider.BEAN_NAME)
    public ExtCaffeineCacheManagerProvider extCaffeineCacheManagerProvider() {
        return new ExtCaffeineCacheManagerProvider();
    }
    
    @Bean(ExtRedisCacheManagerProvider.BEAN_NAME)
    public ExtRedisCacheManagerProvider extRedisCacheManagerProvider() {
        return new ExtRedisCacheManagerProvider();
    }
    
    @Bean(ExtCacheChecker.BEAN_NAME)
    public ExtCacheChecker extCacheChecker() {
        return new ExtCacheChecker();
    }
    
    @Bean(ExtCacheAroundHandlerAdvice.BEAN_NAME)
    public ExtCacheAroundHandlerAdvice extCacheAroundHandler() {
        return new ExtCacheAroundHandlerAdvice();
    }
    
    @Bean(EnableExtSpringCacheListener.BEAN_NAME)
    public EnableExtSpringCacheListener enableExtSpringCacheListener() {
        return new EnableExtSpringCacheListener();
    }
    
    @Bean(ExtRedisCacheInitParser.BEAN_NAME)
    public ExtRedisCacheInitParser extRedisCacheInitParser() {
        return new ExtRedisCacheInitParser();
    }
    
    @Bean(ExtCaffeineCacheInitParser.BEAN_NAME)
    public ExtCaffeineCacheInitParser extCaffeineCacheInitParser() {
        return new ExtCaffeineCacheInitParser();
    }
    
    @DependsOn(ExtRedisCacheInitParser.BEAN_NAME)
    @Bean(name = ExtCacheManager.BEAN_NAME, autowireCandidate = false)
    public ExtCacheManager extCacheManager() {
        return new ExtCacheManager();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.publishEvent(new EnableExtSpringCacheEvent(System.currentTimeMillis()));
    }

}
