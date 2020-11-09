package com.niantou.springcacheext.cache.parser;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.EnableExtCache;
import com.niantou.springcacheext.cache.annotation.CaffeineOop;
import com.niantou.springcacheext.cache.annotation.ExtCacheable;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.annotation.RedisOop;
import com.niantou.springcacheext.cache.support.ExtCacheAroundHandlerAdvice;
import com.niantou.springcacheext.cache.support.ExtCacheHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collection;
import java.util.Set;

import static com.niantou.springcacheext.cache.support.ExtCacheHelper.LOG_PREFIX;

/**
 * ext-cache检查器
 *
 * @author {@link JustryDeng}
 * @since 2020/11/8 5:05:09
 */
public class ExtCacheChecker implements ExtCacheInitParserBeanPostProcessor, ApplicationContextAware {
    
    public static final String BEAN_NAME = "extCacheChecker";
    
    private ApplicationContext applicationContext;
    
    /**
     * 切面优先级检查
     * <p>
     * 注: ext-spring-cache的优先级必须比ExtCacheAroundHandlerAdvice的优先级低,
     *     否者{@link ExtCacheHelper#determineExtCacheOop}中
     *     <code>Stack<ExtCacheCounter> stack = SafeContainer.THREAD_LOCAL_CACHE_COUNTER.get();</code>拿到的就是null
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        EnableExtCache annotation = AnnotationUtils.findAnnotation(clazz, EnableExtCache.class);
        if (annotation == null) {
            return bean;
        }
        int springCacheProxyOrder = annotation.order();
        // ext-spring-cache的优先级必须比ExtCacheAroundHandlerAdvice的优先级低
        Assert.isTrue(ExtCacheAroundHandlerAdvice.ORDER < springCacheProxyOrder,
                LOG_PREFIX + "ext-spring-cache proxy's priority({@link EnableExtCache#order}) must below "
                + "ExtCacheAroundHandlerAdvice's priority({@link ExtCacheAroundHandlerAdvice#getOrder})");
        return bean;
    }
    
    
    /**
     * 校验cacheName不能为空
     *
     * @param extCacheable
     *            前@ExtCacheable信息
     * @param clazz
     *            被当前@ExtCacheable标注的方法所在的类
     * @param method
     *            被当前@ExtCacheable标注的方法
     */
    public void checkCacheNameNonEmpty(@NonNull ExtCacheable extCacheable, Class<?> clazz, Method method) {
        String[] cacheNames = extCacheable.cacheNames();
        Assert.isTrue(!ArrayUtils.isEmpty(cacheNames), String.format(LOG_PREFIX + " cacheNames cannot be empty, config at class[%s]  method[%s]",
                clazz.getName(), method.getName()));
    }
    
    /**
     * 校验redis设置的过期时间不能为负
     *
     * @param oop
     *            ExtCacheable对应的参数模型
     */
    public void checkRedisAnnotationInfo(ExtCacheableOop oop) {
        RedisOop redisOop = oop.getRedis();
        Duration duration = Duration.of(redisOop.getExpireTime(), redisOop.getTimeUnit());
        Assert.isTrue(!(duration.isNegative() || duration.isZero()), String.format(LOG_PREFIX + " redis expireTime cannot be "
                        + "negative or zero(curr duration is [%s]), config at class[%s]  method[%s]",
                duration.toString(), oop.getClazz().getName(), oop.getMethod().getName()));
    }
    
    /**
     * 校验caffeine设置的过期时间不能为负
     *
     * @param oop
     *            ExtCacheable对应的参数模型
     */
    public void checkCaffeineAnnotationInfo(ExtCacheableOop oop) {
        CaffeineOop caffeine = oop.getCaffeine();
        // 过期时间必要要为正
        Duration duration = Duration.of(caffeine.getExpireTime(), caffeine.getTimeUnit());
        Assert.isTrue(!(duration.isNegative() || duration.isZero()), String.format(LOG_PREFIX + " caffeine expireTime cannot be "
                        + "negative or zero(curr duration is [%s]), config at class[%s]  method[%s]",
                duration.toString(), oop.getClazz().getName(), oop.getMethod().getName()));
        // maximumSize和maximumWeight不能同时小于等于0
        long maximumSize = caffeine.getMaximumSize();
        long maximumWeight = caffeine.getMaximumWeight();
        boolean maximumSizeIsLegal = maximumSize > 0;
        boolean maximumWeightIsLegal = maximumWeight > 0;
        Assert.isTrue(maximumSizeIsLegal || maximumWeightIsLegal, String.format(LOG_PREFIX + " maximumSize and maximumWeight"
                        + "cannot less than or equal to 0 at the same time, config at class[%s]  method[%s]",
                 oop.getClazz().getName(), oop.getMethod().getName()));
        // 如果启用了maximumWeight的话(即: maximumSize<0 && maximumWeight > 0), 那么容器中需要有指定name的bean存在才行
        if (!maximumSizeIsLegal) {
            String weigher4MaximumWeight = caffeine.getWeigher4MaximumWeight();
            if (!applicationContext.containsBean(weigher4MaximumWeight)) {
                throw new IllegalArgumentException(String.format(LOG_PREFIX + " cannot find bean[%s] from "
                        + "spring-context, config at class[%s]  method[%s]", weigher4MaximumWeight, oop.getClazz().getName(),
                        oop.getMethod().getName()));
            }
        }
        // 如果启用了refreshAfterWrite的话(即:refreshAfterWrite > 0), 那么容器中需要有指定name的bean存在才行
        int refreshAfterWrite = caffeine.getRefreshAfterWrite();
        if (refreshAfterWrite > 0) {
            String cacheLoader4Refresh = caffeine.getCacheLoader4Refresh();
            if (!applicationContext.containsBean(cacheLoader4Refresh)) {
                throw new IllegalArgumentException(String.format(LOG_PREFIX + " cannot find bean[%s] from "
                                + "spring-context, config at class[%s]  method[%s]", cacheLoader4Refresh, oop.getClazz().getName(),
                        oop.getMethod().getName()));
            }
        }
    }
    
    /**
     * 校验useTemplateName是否合法
     * <p>
     * P.S. 注解上指定的useTemplateName， 在容器中必须有对应的RedisTemplate， 否者抛出异常，使启动失败。
     *
     * @param redisTemplateNameSet
     *            容器中所有可用的RedisTemplate bean-name
     * @param useTemplateName
     *            开发同学指定的RedisTemplate bean-name
     * @param oopCollection
     *            所有指定RedisTemplate bean-name为useTemplateName的ExtCacheableOop集合
     */
    public void validRedisTemplateName(Set<String> redisTemplateNameSet, String useTemplateName, Collection<ExtCacheableOop> oopCollection) {
        if (redisTemplateNameSet.contains(useTemplateName)) {
            return;
        }
        StringBuilder sb = new StringBuilder(64);
        sb.append(LOG_PREFIX);
        for (ExtCacheableOop co : oopCollection) {
            sb.append(String.format("\n\t### see class[%s], method[%s]", co.getClazz().getName(), co.getMethod().getName()));
        }
        throw new IllegalArgumentException(
                String.format(LOG_PREFIX + "there is no any RedisTemplate(or its sub-class) match bean-name [%s]. %s", useTemplateName, sb)
        );
    }
    
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
