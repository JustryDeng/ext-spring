package com.niantou.springcacheext.cache.parser;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.Caffeine;
import com.niantou.springcacheext.cache.annotation.CaffeineOop;
import com.niantou.springcacheext.cache.annotation.ExtCacheable;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.annotation.Redis;
import com.niantou.springcacheext.cache.annotation.RedisOop;
import com.niantou.springcacheext.cache.enums.ExtCacheTypeEnum;
import com.niantou.springcacheext.cache.support.SafeContainer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;

import static com.niantou.springcacheext.cache.support.ExtCacheHelper.LOG_PREFIX;

/**
 * (相对于spring, 附加的)缓存信息解析器
 *
 * @author {@link JustryDeng}
 * @since 2020/11/7 13:05:29
 */
@SuppressWarnings("unused")
public abstract class AbstractExtCacheInitParser implements ExtCacheInitParserBeanPostProcessor {
    
    @Resource(name = ExtCacheChecker.BEAN_NAME)
    private ExtCacheChecker extCacheChecker;
    
    
    @Override
    @SuppressWarnings("NullableProblems")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        // ExtCacheable注解是基于代理实现的， 不可见的方法是不会走代理的， 这里就没必要使用getDeclaredMethods()来获取全量的了
        Method[] accessibleMethodArray = clazz.getMethods();
        if (ArrayUtils.isEmpty(accessibleMethodArray)) {
            return bean;
        }
        Arrays.stream(accessibleMethodArray).forEach(method -> {
            ExtCacheable annotation = AnnotationUtils.findAnnotation(method, ExtCacheable.class);
            if (annotation == null) {
                return;
            }
            // 校验并抽取附件的缓存信息
            Pair<RedisOop, CaffeineOop> pair = checkAndExtractAdditionalCacheInfo(annotation, clazz, method);
            // 解析ExtCacheable注解信息，生成对应的(ExtCacheableOop)模型
            ExtCacheableOop oop = buildExtCacheableOop(annotation, clazz, method, pair.getLeft(), pair.getRight());
            ExtCacheTypeEnum extCacheTypeEnum = ExtCacheTypeEnum.parseCacheType(oop);
            // 公共再解析逻辑
            commonParseLogic(oop, extCacheTypeEnum);
        });
        return bean;
    }
    
    /**
     * 公共再解析逻辑
     *
     * @author {@link JustryDeng}
     * @since 2020/11/9 15:03:56
     */
    private void commonParseLogic(ExtCacheableOop oop, ExtCacheTypeEnum extCacheTypeEnum) {
        // 操作相关安全元素， 进行相关数据的初始化
        SafeContainer.EXT_CACHEABLE_CONTAINER.add(oop);
        SafeContainer.initCommonDataAssign();
        switch (extCacheTypeEnum) {
            case REDIS:
                redisParserLogic(oop);
                break;
            case CAFFEINE:
                caffeineParserLogic(oop);
                break;
            case REDIS_CAFFEINE:
                redisCaffeineParserLogic(oop);
                break;
            default:
                throw new IllegalArgumentException(LOG_PREFIX + "cannot support switch for enum [" + extCacheTypeEnum + "], \n\t curr ExtCacheableOop is -> " + oop);
        }
    }
    
    /**
     * redis再分析
     *
     * @param extCacheableOop
     *         当前缓存信息
     */
    public void redisParserLogic(ExtCacheableOop extCacheableOop) {
        // ext for sub-class
    }
    
    /**
     * caffeine再分析
     *
     * @param extCacheableOop
     *         当前缓存信息
     */
    public void caffeineParserLogic(ExtCacheableOop extCacheableOop) {
        // ext for sub-class
    }
    
    /**
     * redis-caffeine(组合)再分析
     *
     * @param extCacheableOop
     *         当前缓存信息
     */
    public void redisCaffeineParserLogic(ExtCacheableOop extCacheableOop) {
        // ext for sub-class
    }
    
    /**
     * 校验并抽取附件的缓存信息
     *
     * @param extCacheable
     *         带解析的注解信息
     * @param clazz
     *         ExtCacheable所处的类
     * @param method
     *         ExtCacheable所处的方法
     *
     * @return <ul>
     *               <li>左-附加的Redis缓存信息</li>
     *               <li>右-附加的Caffeine缓存信息</li>
     *         </ul>
     */
    protected Pair<RedisOop, CaffeineOop> checkAndExtractAdditionalCacheInfo(ExtCacheable extCacheable,
                                                                             Class<?> clazz, Method method) {
        // 校验cacheName不能为空
        extCacheChecker.checkCacheNameNonEmpty(extCacheable, clazz, method);
        
        Redis[] redisArray = extCacheable.redis();
        Caffeine[] caffeineArray = extCacheable.caffeine();
        boolean redisArrayIsEmpty = ArrayUtils.isEmpty(redisArray);
        boolean caffeineArrayIsEmpty = ArrayUtils.isEmpty(caffeineArray);
        // redisArray 和 localArray不能同时为空
        if (redisArrayIsEmpty && caffeineArrayIsEmpty) {
            throw new IllegalArgumentException(String.format(LOG_PREFIX + "ExtCacheable config at class[%s]#[%s] is "
                    + "wrong. redis and caffeine cannot be empty at the same time ", clazz.getName(),
                    method.getName()));
        }
        
        // 如果redisArray不为空的话
        RedisOop redisOop = null;
        if (!redisArrayIsEmpty) {
            // @ExtCacheable注解中的redis最多只能填写一个
            Assert.isTrue(redisArray.length <= 1, String.format(LOG_PREFIX + " Redis(in @ExtCacheable) excepted size"
                        + " <= 1, but found size is %s at class[%s]#[%s]", redisArray.length, clazz.getName(), method.getName()));
            Redis redis = redisArray[0];
            redisOop = RedisOop.builder().useRedisTemplate(redis.useRedisTemplate())
                    .expireTime(redis.expireTime())
                    .timeUnit(redis.timeUnit())
                    .expireStrategy(redis.expireStrategy())
                    .build();
        }
        
        // 如果caffeineArray不为空的话
        CaffeineOop caffeineOop = null;
        if (!caffeineArrayIsEmpty) {
            // @ExtCacheable注解中的caffeine最多只能填写一个
            Assert.isTrue(caffeineArray.length <= 1, String.format(LOG_PREFIX + " Caffeine(in @ExtCacheable) excepted "
                        + "size <= 1, but found size is %s at class[%s]#[%s]", caffeineArray.length, clazz.getName(), method.getName()));
            Caffeine caffeine = caffeineArray[0];
            caffeineOop = CaffeineOop.builder()
                    .maximumSize(caffeine.maximumSize())
                    .maximumWeight(caffeine.maximumWeight())
                    .weigher4MaximumWeight(caffeine.weigher4MaximumWeight())
                    .initialCapacity(caffeine.initialCapacity())
                    .refreshAfterWrite(caffeine.refreshAfterWrite())
                    .timeUnit4Refresh(caffeine.timeUnit4Refresh())
                    .cacheLoader4Refresh(caffeine.cacheLoader4Refresh())
                    .recordStats(caffeine.recordStats())
                    .keyQuoteType(caffeine.keyQuoteType())
                    .valueQuoteType(caffeine.valueQuoteType())
                    .expireTime(caffeine.expireTime())
                    .timeUnit(caffeine.timeUnit())
                    .expireStrategy(caffeine.expireStrategy())
                    .build();
        }
        return Pair.of(redisOop, caffeineOop);
    }
    
    /**
     * 解析ExtCacheable注解信息，生成对应的(ExtCacheableOop)模型
     *
     * @param extCacheable
     *         带解析的注解信息
     * @param clazz
     *         ExtCacheable所处的类
     * @param method
     *         ExtCacheable所处的方法
     * @param redisOop
     *         附加的Redis缓存信息
     * @param caffeineOop
     *         附加的Caffeine缓存信息
     *
     * @return 与ExtCacheable对应的数据模型ExtCacheableOop
     */
    protected ExtCacheableOop buildExtCacheableOop(ExtCacheable extCacheable, Class<?> clazz, Method method,
                                                   RedisOop redisOop, CaffeineOop caffeineOop) {
        return ExtCacheableOop.builder()
                .clazz(clazz)
                .method(method)
                .redis(redisOop)
                .caffeine(caffeineOop)
                .cacheNames(extCacheable.cacheNames())
                .key(extCacheable.key())
                .value(extCacheable.value())
                .keyGenerator(extCacheable.keyGenerator())
                .cacheManager(extCacheable.cacheManager())
                .cacheResolver(extCacheable.cacheResolver())
                .condition(extCacheable.condition())
                .unless(extCacheable.unless())
                .sync(extCacheable.sync())
                .build();
    }
}
