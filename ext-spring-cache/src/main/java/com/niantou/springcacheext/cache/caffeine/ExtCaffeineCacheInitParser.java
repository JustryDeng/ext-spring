package com.niantou.springcacheext.cache.caffeine;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.parser.AbstractExtCacheInitParser;
import com.niantou.springcacheext.cache.parser.ExtCacheChecker;
import com.niantou.springcacheext.cache.support.SafeContainer;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * ext-caffeine-cache初始化解析器
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 18:44:39
 */
@Slf4j
public class ExtCaffeineCacheInitParser extends AbstractExtCacheInitParser {
    
    public static final String BEAN_NAME = "extCaffeineCacheInitParser";
    
    @Resource(name = ExtCacheChecker.BEAN_NAME)
    private ExtCacheChecker extCacheChecker;
    
    @Override
    public void caffeineParserLogic(ExtCacheableOop extCacheableOop) {
        extCacheChecker.checkCaffeineAnnotationInfo(extCacheableOop);
        SafeContainer.Data4Caffeine.initRedisData();
    }
    
}