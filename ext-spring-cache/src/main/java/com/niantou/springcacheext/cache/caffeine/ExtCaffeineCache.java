package com.niantou.springcacheext.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.support.SafeContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cache.caffeine.CaffeineCache;

/**
 * ext CaffeineCache
 * <p>
 * @author {@link JustryDeng}
 * @since 2020/11/8 15:47:10
 */
@Slf4j
@SuppressWarnings("unused")
public class ExtCaffeineCache extends CaffeineCache {
    
    public ExtCaffeineCache(String name, Cache<Object, Object> cache) {
        super(name, cache);
    }
    
    public ExtCaffeineCache(String name, Cache<Object, Object> cache, boolean allowNullValues) {
        super(name, cache, allowNullValues);
    }
    
    
    @Override
    public ValueWrapper get(Object key) {
        Boolean refreshCurrCache = SafeContainer.THREAD_LOCAL_REFRESH_CURR_CACHE.get();
        if (BooleanUtils.isTrue(refreshCurrCache)) {
            // 这里返回null, 那么程序就会走方法里面的逻辑， 走完后再把结果放入缓存， 继而：变相实现了刷新当前key的缓存
            log.info(" refresh curr key[{}]'s cache-value", key);
            return null;
        }
        ValueWrapper valueWrapper = super.get(key);
        if (valueWrapper != null) {
            log.info(" got non-null valueWrapper from caffeine-cache, by key[{}]", key);
        }
        return valueWrapper;
    }
}