package com.niantou.springcacheext.cache.annotation;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.enums.RedisExpireStrategyEnum;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.temporal.ChronoUnit;

/**
 * redis缓存相关信息
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 14:50:29
 */
public @interface Redis {
    
    /** a spring-bean name who type of {@link RedisTemplate}. of curse, including its sub-classes. */
    String useRedisTemplate();
    
    /** expire-time (0 represent never expire) */
    int expireTime() default 30;
    
    /** unit for {@link Redis#expireTime()} */
    ChronoUnit timeUnit() default ChronoUnit.SECONDS;
    
    /** strategy for {@link Redis#expireTime()} */
    RedisExpireStrategyEnum expireStrategy() default RedisExpireStrategyEnum.AUTO;
}
