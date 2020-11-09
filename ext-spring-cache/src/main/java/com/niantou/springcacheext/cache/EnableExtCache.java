package com.niantou.springcacheext.cache;

import com.niantou.springcacheext.author.JustryDeng;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用ExtCache
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 17:56:18
 */
@SuppressWarnings("unused")
@Documented
@EnableCaching
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration(ExtCacheConfiguration.class)
public @interface EnableExtCache {
    
    @AliasFor(annotation = EnableCaching.class)
    boolean proxyTargetClass() default false;
    
    @AliasFor(annotation = EnableCaching.class)
    AdviceMode mode() default AdviceMode.PROXY;
    
    /**
     * 注意: 此ORDER的值需要比ExtCacheAroundHandlerAdvice#getOrder()的值大
     */
    @AliasFor(annotation = EnableCaching.class)
    int order() default Ordered.LOWEST_PRECEDENCE;
    
}
