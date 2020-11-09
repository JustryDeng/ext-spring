package com.niantou.springcacheext.cache.redis_caffeine;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.parser.AbstractExtCacheInitParser;
import com.niantou.springcacheext.cache.parser.ExtCacheChecker;
import com.niantou.springcacheext.cache.support.SafeContainer;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * ext-redis-caffeine-cache初始化解析器
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 18:44:39
 */
@Slf4j
public class ExtRedisCaffeineCacheInitParser extends AbstractExtCacheInitParser {
    
    public static final String BEAN_NAME = "extRedisCaffeineCacheInitParser";

    @Resource(name = ExtCacheChecker.BEAN_NAME)
    private ExtCacheChecker extCacheChecker;
    
    @Override
    public void redisCaffeineParserLogic(ExtCacheableOop extCacheableOop) {
        extCacheChecker.checkRedisAnnotationInfo(extCacheableOop);
        extCacheChecker.checkCaffeineAnnotationInfo(extCacheableOop);
        SafeContainer.Data4RedisCaffeine.initRedisData();
    }
}
