package com.niantou.springcacheext.cache.annotation;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.manager.ExtCacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扩展{@link Cacheable}
 *
 * P.S. 现版本IDE的SpringCacheNamesInspection检查，识别不了继承注解的写法， 会报黄；
 *      可以直接@SuppressWarnings({"SpringCacheNamesInspection"})掉。
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 14:50:29
 */
@Cacheable
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings({"unused", "SpringCacheNamesInspection"})
public @interface ExtCacheable {
    
    /** redis-cache (make sure size &lt;= 1) */
    Redis[] redis() default {};
    
    /** caffeine-cache (make sure size &lt;= 1) */
    Caffeine[] caffeine() default {};
    
    /** @see Cacheable#cacheNames()  */
    @AliasFor(annotation = Cacheable.class)
    String[] cacheNames() default {};
    
    /** @see Cacheable#key()  */
    @AliasFor(annotation = Cacheable.class)
    String key() default "";
    
    /** @see Cacheable#value()  */
    @AliasFor(annotation = Cacheable.class)
    String[] value() default {};
    
    /** @see Cacheable#keyGenerator()  */
    @AliasFor(annotation = Cacheable.class)
    String keyGenerator() default "";
    
    /** @see Cacheable#cacheManager()  */
    @AliasFor(annotation = Cacheable.class)
    String cacheManager() default ExtCacheManager.BEAN_NAME;
    
    /** @see Cacheable#cacheResolver()  */
    @AliasFor(annotation = Cacheable.class)
    String cacheResolver() default "";
    
    /** @see Cacheable#condition()  */
    @AliasFor(annotation = Cacheable.class)
    String condition() default "";
    
    /** @see Cacheable#unless()  */
    @AliasFor(annotation = Cacheable.class)
    String unless() default "";
    
    /** @see Cacheable#sync()  */
    @AliasFor(annotation = Cacheable.class)
    boolean sync() default false;
}
