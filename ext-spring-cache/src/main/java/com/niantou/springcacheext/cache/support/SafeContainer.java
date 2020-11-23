package com.niantou.springcacheext.cache.support;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.annotation.ExtCacheableOop;
import com.niantou.springcacheext.cache.annotation.RedisOop;
import com.niantou.springcacheext.cache.enums.ExtCacheTypeEnum;
import com.niantou.springcacheext.cache.model.ExtCacheCounter;
import com.niantou.springcacheext.cache.model.ViaMethod;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import static com.niantou.springcacheext.cache.support.ExtCacheHelper.LOG_PREFIX;

/**
 * 一个(简易的)安全类
 *
 * @author {@link JustryDeng}
 * @since 2020/11/7 15:46:35
 */
public final class SafeContainer {
    
    private SafeContainer() {
        throw new UnsupportedOperationException("safe-class cannot allow to been overrode");
    }
    
    /** 线程method_stack */
    @SuppressWarnings("AlibabaThreadLocalShouldRemove")
    public static final ThreadLocal<Stack<ExtCacheCounter>> THREAD_LOCAL_CACHE_COUNTER = new ThreadLocal<>();
    
    /** 标识：是否刷新当前缓存 */
    @SuppressWarnings("AlibabaThreadLocalShouldRemove")
    public static final ThreadLocal<Boolean> THREAD_LOCAL_REFRESH_CURR_CACHE = new ThreadLocal<>();
    
    /** ExtCacheableOop容器 */
    public static final List<ExtCacheableOop> EXT_CACHEABLE_CONTAINER =
            Collections.synchronizedList(new ArrayList<>(64));
    
    /** <literal>ViaMethod - ExtCacheableOop> map</literal> */
    private static Map<ViaMethod, ExtCacheableOop> VIA_METHOD_AND_OOP_MAP = ImmutableMap.of();
    
    public static Map<ViaMethod, ExtCacheableOop> viaMethodAndOopMap() {
        return VIA_METHOD_AND_OOP_MAP;
    }
    
    /**
     * 将{@link SafeContainer#EXT_CACHEABLE_CONTAINER}中的元素进行初始化归档分配
     * <p>
     * 注: 因为只有在程序启动初始化时，才会调用此方法， 所以这里选择忍受直接将synchronized加在class上
     */
    public synchronized static void initCommonDataAssign() {
        Map<ViaMethod, ExtCacheableOop> viaMethodAndOopMap = new HashMap<>(32);
        // -> 填充数据至 相关容器
        EXT_CACHEABLE_CONTAINER.stream().filter(Objects::nonNull).forEach(ext -> {
            String[] cacheNames = ext.getCacheNames();
            if (ArrayUtils.isEmpty(cacheNames)) {
                return;
            }
            // init VIA_METHOD_AND_OOP_MAP
            viaMethodAndOopMap.put(ViaMethod.getInstance(ext.getMethod()), ext);
        });
        VIA_METHOD_AND_OOP_MAP = ImmutableMap.copyOf(viaMethodAndOopMap);
    }
    
    /**
     * redis用到的相关数据
     *
     * @author {@link JustryDeng}
     * @since 2020/11/9 15:07:23
     */
    public final static class Data4Redis {
        
        private Data4Redis() {
            throw new UnsupportedOperationException("safe-class cannot allow to been overrode");
        }
        
        /** <literal>cacheName - Collection<ExtCacheableOop> map</literal> */
        private static Multimap<String, ExtCacheableOop> CACHE_NAME_AND_OOP_MAP = ImmutableMultimap.of();
        
        /** <literal>redisTemplate - Collection<ExtCacheableOop> map</literal> */
        private static Multimap<String, ExtCacheableOop> REDIS_TEMPLATE_NAME_AND_OOP_MAP = ImmutableMultimap.of();
        
        /** ExtCacheableOop容器 */
        public static final List<ExtCacheableOop> EXT_REDIS_CONTAINER =
                Collections.synchronizedList(new ArrayList<>(64));
        
        
        public static Multimap<String, ExtCacheableOop> cacheNameAndOopMap() {
            return CACHE_NAME_AND_OOP_MAP;
        }
        
        public static Multimap<String, ExtCacheableOop> redisTemplateNameAndOopMap() {
            return REDIS_TEMPLATE_NAME_AND_OOP_MAP;
        }
        
        /**
         * 从{@link SafeContainer#EXT_CACHEABLE_CONTAINER}中抽取redis专有的数据， 并归档
         * <p>
         * 注: 因为只有在程序启动初始化时，才会调用此方法， 所以这里选择忍受直接将synchronized加在class上
         */
        public synchronized static void initRedisData() {
            // 从EXT_CACHEABLE_CONTAINER中分离 redis专有的ExtCacheableOop
            EXT_CACHEABLE_CONTAINER.stream()
                    .filter(oop -> isSpecifiedTypeOopData(oop, ExtCacheTypeEnum.REDIS))
                    .forEach(EXT_REDIS_CONTAINER::add);
            
            Multimap<String, ExtCacheableOop> cacheNameAndOopMap = HashMultimap.create();
            Multimap<String, ExtCacheableOop> redisTemplateNameAndOopMap = HashMultimap.create();
            // -> 填充数据至 相关容器
            EXT_REDIS_CONTAINER.stream().filter(Objects::nonNull).forEach(ext -> {
                String[] cacheNames = ext.getCacheNames();
                if (ArrayUtils.isEmpty(cacheNames)) {
                    return;
                }
                // init CACHE_NAME_AND_OOP_MAP
                for (String cacheName : cacheNames) {
                    cacheNameAndOopMap.put(cacheName, ext);
                }
                
                // init REDIS_TEMPLATE_NAME_AND_OOP_MAP
                RedisOop redis = ext.getRedis();
                if (redis != null) {
                    redisTemplateNameAndOopMap.put(redis.getUseRedisTemplate(), ext);
                }
            });
            
            CACHE_NAME_AND_OOP_MAP = ImmutableMultimap.copyOf(cacheNameAndOopMap);
            REDIS_TEMPLATE_NAME_AND_OOP_MAP = ImmutableMultimap.copyOf(redisTemplateNameAndOopMap);
        }
    }
    
    /**
     * caffeine用到的相关数据
     *
     * @author {@link JustryDeng}
     * @since 2020/11/9 15:07:23
     */
    public final static class Data4Caffeine {
        
        /** ExtCacheableOop容器 */
        public static final Set<ExtCacheableOop> EXT_CAFFEINE_CONTAINER = Collections.synchronizedSet(new HashSet<>());
        
        private Data4Caffeine() {
            throw new UnsupportedOperationException("safe-class cannot allow to been overrode");
        }
        
        /**
         * 从{@link SafeContainer#EXT_CACHEABLE_CONTAINER}中抽取caffeine专有的数据
         * <p>
         * 注: 因为只有在程序启动初始化时，才会调用此方法， 所以这里选择忍受直接将synchronized加在class上
         */
        public synchronized static void initRedisData() {
            EXT_CACHEABLE_CONTAINER.stream()
                    .filter(oop -> isSpecifiedTypeOopData(oop, ExtCacheTypeEnum.CAFFEINE))
                    .forEach(EXT_CAFFEINE_CONTAINER::add);
        }
    }
    
    /**
     * redis-caffeine用到的相关数据
     *
     * @author {@link JustryDeng}
     * @since 2020/11/9 15:07:23
     */
    public final static class Data4RedisCaffeine {
        
        private Data4RedisCaffeine() {
            throw new UnsupportedOperationException("safe-class cannot allow to been overrode");
        }
        
        /** ExtCacheableOop容器 */
        public static final List<ExtCacheableOop> EXT_REDIS_CAFFEINE_CONTAINER =
                Collections.synchronizedList(new ArrayList<>(64));
        
        /** <literal>redisTemplate - Collection<ExtCacheableOop> map</literal> */
        private static Multimap<String, ExtCacheableOop> REDIS_TEMPLATE_NAME_AND_OOP_MAP = ImmutableMultimap.of();
        
        public static Multimap<String, ExtCacheableOop> redisTemplateNameAndOopMap() {
            return REDIS_TEMPLATE_NAME_AND_OOP_MAP;
        }
        
        /**
         * 从{@link SafeContainer#EXT_CACHEABLE_CONTAINER}中抽取redis_caffeine专有的数据， 并归档
         * <p>
         * 注: 因为只有在程序启动初始化时，才会调用此方法， 所以这里选择忍受直接将synchronized加在class上
         */
        public synchronized static void initRedisData() {
            // 从EXT_CACHEABLE_CONTAINER中分离 redis专有的ExtCacheableOop
            EXT_CACHEABLE_CONTAINER.stream()
                    .filter(oop -> isSpecifiedTypeOopData(oop, ExtCacheTypeEnum.REDIS_CAFFEINE))
                    .forEach(EXT_REDIS_CAFFEINE_CONTAINER::add);
            
            Multimap<String, ExtCacheableOop> redisTemplateNameAndOopMap = HashMultimap.create();
            // -> 填充数据至 相关容器
            EXT_REDIS_CAFFEINE_CONTAINER.stream().filter(Objects::nonNull).forEach(ext -> {
                String[] cacheNames = ext.getCacheNames();
                if (ArrayUtils.isEmpty(cacheNames)) {
                    return;
                }
                // init REDIS_TEMPLATE_NAME_AND_OOP_MAP
                RedisOop redis = ext.getRedis();
                if (redis != null) {
                    redisTemplateNameAndOopMap.put(redis.getUseRedisTemplate(), ext);
                }
            });
            REDIS_TEMPLATE_NAME_AND_OOP_MAP = ImmutableMultimap.copyOf(redisTemplateNameAndOopMap);
        }
    }
    
    /**
     * 判断oop是否是指定缓存类型的数据
     *
     * @param oop
     *         要判断的数据对象
     * @param type
     *         指定的类型
     *
     * @return 是否是指定缓存类型的数据
     */
    private static boolean isSpecifiedTypeOopData(ExtCacheableOop oop, ExtCacheTypeEnum type) {
        if (oop == null) {
            return false;
        }
        switch (type) {
            case REDIS:
                return oop.getRedis() != null && oop.getCaffeine() == null;
            case CAFFEINE:
                return oop.getRedis() == null && oop.getCaffeine() != null;
            case REDIS_CAFFEINE:
                return oop.getRedis() != null && oop.getCaffeine() != null;
            default:
                throw new IllegalArgumentException(LOG_PREFIX + "cannot support switch for enum [" + type + "], \n\t "
                        + "curr ExtCacheableOop is -> " + oop);
        }
    }
}