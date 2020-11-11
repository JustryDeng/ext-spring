package com.niantou.springcacheext.cache.provider;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.caffeine.ExtCaffeineCacheManager;
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
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.niantou.springcacheext.cache.constant.ExtCacheConfigPlaceholder.CAFFEINE_RESPONSE_SPRING_CONTEXT_PLACEHOLDER;

/**
 * redis Caffeine provider, 用于管理所有的 ExtCaffeineCacheManager
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 12:22:26
 */
@Slf4j
@DependsOn
public class ExtCaffeineCacheManagerProvider implements CacheManagerProvider {
    
    public static final String BEAN_NAME = "extCaffeineCacheManagerProvider";
    
    /**
     * true:
     *      默认对spring-context中的配置作出响应。  即：在spring-context中若存在相应配置或相关bean，那么会影响所有的Caffeine。
     *      此时，可通过在使用@Caffeine注解时，显示的指定相关配置来覆盖 spring-context中的配置
     *
     * false:
     *      默认不对spring-context中的配置作出响应。即：不管spring-context是否存在相应配置或相关bean，都不会影响Caffeine。
     *      此时，在使用@Caffeine注解时，显示的指定相关配置依然有效
     */
    @Value(CAFFEINE_RESPONSE_SPRING_CONTEXT_PLACEHOLDER)
    private boolean responseSpringContext;
    
    @Autowired(required = false)
    private CacheProperties cacheProperties;
    
    @Autowired
    private ObjectProvider<Caffeine<Object, Object>> caffeineProvider;
    
    @Autowired
    private org.springframework.beans.factory.ObjectProvider<CaffeineSpec> caffeineSpecProvider;
    
    @Autowired
    private ObjectProvider<CacheLoader<Object, Object>> cacheLoaderProvider;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private final Map<ExtCacheableOop, ExtCaffeineCacheManager> caffeineCacheManagerMap = new ConcurrentHashMap<>(8);
    
    @Override
    @Nullable
    public CacheManager provide(ExtCacheableOop oop) {
        return caffeineCacheManagerMap.get(oop);
    }
    
    /**
     * 为了保留Caffeine原有的对spring-context中个组件/配置的支持，
     * 这里初始化的数据直接从spring-context中拿(, 而不写死)
     * <p>
     * P.S. 如果初始化时，想使自定义的caffeine与spring-context中可能存在的相关caffeine配置不搭噶，
     *       那么只需要这里builder的时候传null或空的provider即可
     */
    @Override
    public void afterSingletonsInstantiated() {
        // -> 初始化caffeineCacheManagerMap
        SafeContainer.Data4Caffeine.EXT_CAFFEINE_CONTAINER.forEach(oop -> {
            ExtCaffeineCacheManager.ExtCaffeineCacheManagerBuilder builder;
            if (responseSpringContext) {
                builder = ExtCaffeineCacheManager.ExtCaffeineCacheManagerBuilder
                        .builder(cacheProperties, caffeineProvider, caffeineSpecProvider, cacheLoaderProvider);
            } else {
                builder = ExtCaffeineCacheManager.ExtCaffeineCacheManagerBuilder
                        .builder(new CacheProperties(), new EmptyObjectProvider<>(), new EmptyObjectProvider<>(), new EmptyObjectProvider<>());
            }
            ExtCaffeineCacheManager cacheManager = builder.custom(
                    ExtCacheHelper.buildCaffeine(oop.getCaffeine(), applicationContext, builder)
            ).build();
            caffeineCacheManagerMap.put(oop, cacheManager);
        });
    }
    

}
