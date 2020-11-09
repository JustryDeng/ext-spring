package com.niantou.springcacheext.cache.annotation;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.enums.CaffeineExpireStrategyEnum;
import com.niantou.springcacheext.cache.enums.CaffeineKeyQuoteTypeEnum;
import com.niantou.springcacheext.cache.enums.CaffeineValueQuoteTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.temporal.ChronoUnit;

/**
 * {@link Caffeine}中的信息的容器类
 *
 * P.S. 每个字段的语意对应Local见中的字段即可
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 19:08:31
 */
@Data
@Builder
@AllArgsConstructor
public class CaffeineOop {
    
    private final long maximumSize;
    
    private final long maximumWeight;
    
    private final String weigher4MaximumWeight;
    
    private final int initialCapacity;
    
    private final int refreshAfterWrite;
    
    private final ChronoUnit timeUnit4Refresh;
    
    private final String cacheLoader4Refresh;
    
    private final boolean recordStats;
    
    private final CaffeineKeyQuoteTypeEnum keyQuoteType;
    
    private final CaffeineValueQuoteTypeEnum valueQuoteType;
    
    private final int expireTime;
    
    private final ChronoUnit timeUnit;
    
    private final CaffeineExpireStrategyEnum expireStrategy;
}