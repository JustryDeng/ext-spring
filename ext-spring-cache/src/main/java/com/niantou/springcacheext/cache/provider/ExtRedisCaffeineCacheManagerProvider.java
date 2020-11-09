package com.niantou.springcacheext.cache.provider;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.caffeine.ExtCaffeineCacheManager;
import com.niantou.springcacheext.cache.parser.ExtCacheChecker;
import com.niantou.springcacheext.cache.redis.ExtRedisCacheManager;
import com.niantou.springcacheext.cache.redis_caffeine.ExtRedisCaffeineCacheManager;
import com.niantou.springcacheext.cache.support.EmptyObjectProvider;
import com.niantou.springcacheext.cache.support.ExtCacheHelper;
import com.niantou.springcacheext.cache.support.SafeContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.niantou.springcacheext.cache.constant.ExtCacheConfigPlaceholderBase.REDIS_CAFFEINE_RESPONSE_SPRING_CONTEXT_PLACEHOLDER;

/**
 * redis-caffeine CacheManager provider, 用于管理所有的 ExtRedisCaffeineCacheManager
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 12:22:26
 */
@Slf4j
@SuppressWarnings({"rawtypes"})
public class ExtRedisCaffeineCacheManagerProvider implements CacheManagerProvider {
    
    public static final String BEAN_NAME = "extRedisCaffeineCacheManagerProvider";
    
    /**
     * true:
     *      默认对spring-context中的配置作出响应。  即：在spring-context中若存在相应配置或相关bean，那么会影响所有的Redis、Caffeine。
     *      此时，可通过在使用@Redis、@Caffeine注解时，显示的指定相关配置来覆盖 spring-context中的配置
     *
     * false:
     *      默认不对spring-context中的配置作出响应。即：不管spring-context是否存在相应配置或相关bean，都不会影响Redis、Caffeine。
     *      此时，在使用@Redis、@Caffeine注解时，显示的指定相关配置依然有效
     */
    @Value(REDIS_CAFFEINE_RESPONSE_SPRING_CONTEXT_PLACEHOLDER)
    private boolean responseSpringContext;
    
    @Autowired(required = false)
    private CacheProperties cacheProperties;
    
    @Autowired(required = false)
    private ClassLoader classLoader;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Resource(name = ExtCacheChecker.BEAN_NAME)
    private ExtCacheChecker extCacheChecker;
    
    @Autowired
    private ObjectProvider<Caffeine<Object, Object>> caffeineProvider;
    
    @Autowired
    private org.springframework.beans.factory.ObjectProvider<CaffeineSpec> caffeineSpecProvider;
    
    @Autowired
    private ObjectProvider<CacheLoader<Object, Object>> cacheLoaderProvider;
    
    private final Map<ExtCacheableOop, ExtRedisCaffeineCacheManager> redisCacheManagerMap = new ConcurrentHashMap<>(8);
    
    @Override
    @Nullable
    public CacheManager provide(ExtCacheableOop oop) {
        return redisCacheManagerMap.get(oop);
    }
    
    @Override
    public void afterSingletonsInstantiated() {
        // 初始化redisCacheManagerMap
        Map<String, RedisTemplate> tmpRedisTemplateMap = applicationContext.getBeansOfType(RedisTemplate.class);
        Set<String> redisTemplateNameSet = tmpRedisTemplateMap.keySet();
        SafeContainer.Data4RedisCaffeine.redisTemplateNameAndOopMap().forEach((k, v) -> {
            Collection<ExtCacheableOop> oopCollection = SafeContainer.Data4RedisCaffeine.redisTemplateNameAndOopMap().get(k);
            // 校验useTemplateName是否合法
            extCacheChecker.validRedisTemplateName(redisTemplateNameSet, k, oopCollection);
            
            RedisTemplate redisTemplate = tmpRedisTemplateMap.get(k);
            ExtRedisCacheManager extRedisCacheManager = generateExtRedisCacheManager(redisTemplate);
            Iterator<ExtCacheableOop> iterator = oopCollection.stream().iterator();
            while (iterator.hasNext()) {
                ExtCacheableOop nextOop = iterator.next();
                ExtCaffeineCacheManager extCaffeineCacheManager = generateExtCaffeineCacheManager(nextOop);
                redisCacheManagerMap.put(nextOop, new ExtRedisCaffeineCacheManager(extRedisCacheManager, extCaffeineCacheManager));
            }
        });
    }
    
    
    /**
     * 生成 ExtRedisCacheManager
     */
    private ExtRedisCacheManager generateExtRedisCacheManager(RedisTemplate redisTemplate) {
        ExtRedisCacheManager extRedisCacheManager;
        if (responseSpringContext) {
            extRedisCacheManager = ExtRedisCacheManager.ExtRedisCacheManagerBuilder.builder(redisTemplate)
                    .customCacheConfig(cacheProperties, classLoader).build();
        } else {
            extRedisCacheManager = ExtRedisCacheManager.ExtRedisCacheManagerBuilder.builder(redisTemplate).build();
        }
        extRedisCacheManager.initializeCaches();
        return extRedisCacheManager;
    }
    
    /**
     * 生成 ExtCaffeineCacheManager
     */
    private ExtCaffeineCacheManager generateExtCaffeineCacheManager(ExtCacheableOop oop) {
        ExtCaffeineCacheManager.ExtCaffeineCacheManagerBuilder builder;
        if (responseSpringContext) {
            builder = ExtCaffeineCacheManager.ExtCaffeineCacheManagerBuilder
                    .builder(cacheProperties, caffeineProvider, caffeineSpecProvider, cacheLoaderProvider);
        } else {
            builder = ExtCaffeineCacheManager.ExtCaffeineCacheManagerBuilder
                    .builder(new CacheProperties(), new EmptyObjectProvider<>(), new EmptyObjectProvider<>(),
                            new EmptyObjectProvider<>());
        }
        return builder.custom(
                ExtCacheHelper.buildCaffeine(oop.getCaffeine(), applicationContext, builder)
        ).build();
    }
}
