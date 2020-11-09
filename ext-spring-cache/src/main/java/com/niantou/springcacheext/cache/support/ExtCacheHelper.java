package com.niantou.springcacheext.cache.support;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.CaffeineOop;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.caffeine.ExtCaffeineCacheManager;
import com.niantou.springcacheext.cache.enums.CaffeineExpireStrategyEnum;
import com.niantou.springcacheext.cache.enums.CaffeineKeyQuoteTypeEnum;
import com.niantou.springcacheext.cache.enums.CaffeineValueQuoteTypeEnum;
import com.niantou.springcacheext.cache.model.ExtCacheCounter;
import com.niantou.springcacheext.cache.model.ViaMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Stack;

/**
 * 图画里，龙不吟虎不啸， 小小书童可笑可笑
 *
 * @author {@link JustryDeng}
 * @since 2020/11/5 21:54:19
 */
@Slf4j
public final class ExtCacheHelper {
    
    /** ext-spring-cache 统一日志前缀 */
    public static final String LOG_PREFIX = "|EXT-SPRING-CACHE|";
    
    private ExtCacheHelper() {
        throw new UnsupportedOperationException("util-class cannot support create-instance");
    }
    
    /**
     * 定位到当前ExtCacheableOop对象
     *
     * @return  当前ExtCacheableOop对象
     */
    @Nullable
    public static ExtCacheableOop determineExtCacheOop() {
        return ExtCacheHelper.determineExtCacheOop(null);
    }
    
    /**
     * 定位到当前命名空间对应的ExtCacheableOop对象
     *
     * @param cacheName
     *            cacheName-命名空间
     * @return  对应的ExtCacheableOop对象
     */
    @Nullable
    public static ExtCacheableOop determineExtCacheOop(@Nullable String cacheName) {
        Stack<ExtCacheCounter> stack = SafeContainer.THREAD_LOCAL_CACHE_COUNTER.get();
        if (stack == null || stack.empty()) {
            log.warn(LOG_PREFIX + " stack(from thread-local) is empty. curr cacheName is -> {}", cacheName);
            return null;
        }
        ExtCacheCounter counter = stack.peek();
        ViaMethod viaMethod = counter.getViaMethod();
        ExtCacheableOop extCacheableOop = SafeContainer.viaMethodAndOopMap().get(viaMethod);
        if (extCacheableOop == null) {
            log.warn(LOG_PREFIX + " extCacheableOop(from thread-local) is null. curr cacheName is -> {}", cacheName);
            return null;
        }
        if (StringUtils.isEmpty(cacheName)) {
            return extCacheableOop;
        }
        // 校验cacheName
        String[] cacheNames = extCacheableOop.getCacheNames();
        if (!ArrayUtils.contains(cacheNames, cacheName)) {
            log.warn(LOG_PREFIX + " cannot found target cacheName in cacheNames. curr cacheName is -> {}, "
                    + "curr extCacheableOop is -> {}", cacheName, extCacheableOop);
            return null;
        }
        return extCacheableOop;
    }
    
    /**
     * 获取Caffeine实例对象
     * <p>
     * P.S. 这里没有在对值进行运用前进行相关校验， 是因为在{@link ExtCaffeineCacheInitParser#caffeineParserLogic(ExtCacheableOop)}
     *      这里就已经完成了相关校验了
     *
     * @param caffeineOop
     *            ExtCacheable注解中@Caffeine对应的信息
     * @param applicationContext
     *            spring-context
     * @param managerBuilder
     *            ExtCaffeineCacheManager建造器
     * @return  Caffeine实例对象
     */
    @SuppressWarnings("unchecked")
    public static Caffeine<Object, Object> buildCaffeine(CaffeineOop caffeineOop, ApplicationContext applicationContext, ExtCaffeineCacheManager.ExtCaffeineCacheManagerBuilder managerBuilder) {
        Caffeine<Object, Object> instance = Caffeine.newBuilder();
        // 初始化容量
        instance.initialCapacity(caffeineOop.getInitialCapacity());
        // 淘汰机制
        long maximumSize = caffeineOop.getMaximumSize();
        if (maximumSize > 0) {
            instance.maximumSize(maximumSize);
        } else {
            instance.maximumWeight(caffeineOop.getMaximumWeight());
            instance.weigher(applicationContext.getBean(caffeineOop.getWeigher4MaximumWeight(), Weigher.class));
        }
        // 刷新机制
        int refreshAfterWrite = caffeineOop.getRefreshAfterWrite();
        if (refreshAfterWrite > 0) {
            instance.refreshAfterWrite(Duration.of(refreshAfterWrite, caffeineOop.getTimeUnit4Refresh()));
            managerBuilder.custom(applicationContext.getBean(caffeineOop.getCacheLoader4Refresh(), CacheLoader.class));
        }
        // 统计(命中率等)信息
        if (caffeineOop.isRecordStats()) {
            instance.recordStats();
        }
        // 过期时间
        Duration duration = Duration.of(caffeineOop.getExpireTime(), caffeineOop.getTimeUnit());
        CaffeineExpireStrategyEnum expireStrategy = caffeineOop.getExpireStrategy();
        if (expireStrategy == CaffeineExpireStrategyEnum.EXPIRE_AFTER_WRITE) {
            instance.expireAfterWrite(duration);
        } else if (expireStrategy == CaffeineExpireStrategyEnum.EXPIRE_AFTER_ACCESS) {
            instance.expireAfterAccess(duration);
        } else {
            throw new IllegalArgumentException(LOG_PREFIX + " cannot discern CaffeineExpireStrategyEnum enum-item [" + expireStrategy + "]");
        }
        // key是否采用虚引用
        if (caffeineOop.getKeyQuoteType() == CaffeineKeyQuoteTypeEnum.WEAK) {
            instance.weakKeys();
        }
        // value是否采用虚引用/软引用
        CaffeineValueQuoteTypeEnum valueQuoteType = caffeineOop.getValueQuoteType();
        if (valueQuoteType == CaffeineValueQuoteTypeEnum.WEAK) {
            instance.weakValues();
        } else if (valueQuoteType == CaffeineValueQuoteTypeEnum.SOFT) {
            instance.softValues();
        } else {
            // default
            log.debug(LOG_PREFIX + " CaffeineValueQuoteTypeEnum used -> {}", valueQuoteType);
        }
        return instance;
    }
}
