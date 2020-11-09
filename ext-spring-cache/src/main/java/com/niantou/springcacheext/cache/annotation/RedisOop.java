package com.niantou.springcacheext.cache.annotation;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.enums.RedisExpireStrategyEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.temporal.ChronoUnit;

/**
 * {@link Redis}中的信息的容器类
 *
 * P.S. 每个字段的语意对应Redis见中的字段即可
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 19:08:31
 */
@Data
@Builder
@AllArgsConstructor
public class RedisOop {
    
    private final String useRedisTemplate;
    
    private final int expireTime;
    
    private final ChronoUnit timeUnit;
    
    private final RedisExpireStrategyEnum expireStrategy;
}
