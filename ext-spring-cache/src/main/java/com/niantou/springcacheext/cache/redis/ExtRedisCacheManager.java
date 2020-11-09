package com.niantou.springcacheext.cache.redis;

import com.niantou.springcacheext.author.JustryDeng;
import com.niantou.springcacheext.cache.support.SafeContainer;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.niantou.springcacheext.cache.support.SafeContainer.EXT_CACHEABLE_CONTAINER;

/**
 * ext-redis 缓存管理器
 *
 * @author {@link JustryDeng}
 * @since 2020/11/4 20:30:21
 */
public class ExtRedisCacheManager extends RedisCacheManager {
    
    protected final RedisCacheWriter cacheWriter;
    protected final RedisCacheConfiguration cacheConfig;
    protected final Map<String, RedisCacheConfiguration> cacheConfigMap;
    
    /**
     * 当然，不初始化extRedisCaches也是可以的， 这样的话，程序在跑着的时候会自动懒加载创建。
     * <p>
     * 不过为了提升性能， 这里选择重写{@link this#initializeCaches}和{@link this#initializeCaches}主动初始化相关ext Cache
     */
    protected final List<RedisCache> extRedisCaches = Collections.synchronizedList(new ArrayList<>(16));
    
    protected ExtRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig,
                                 Map<String, RedisCacheConfiguration> initialCacheConfigurations,
                                 boolean allowInFlightCacheCreation) {
        super(cacheWriter, cacheConfig, initialCacheConfigurations, allowInFlightCacheCreation);
        this.cacheWriter = cacheWriter;
        this.cacheConfig = cacheConfig;
        this.cacheConfigMap = initialCacheConfigurations;
    }
    
    @NonNull
    @Override
    public RedisCache createRedisCache(@NonNull String name, RedisCacheConfiguration cacheConfig) {
        return new ExtRedisCache(name, cacheWriter, cacheConfig, cacheConfigMap == null ? new HashMap<>(1) : cacheConfigMap);
    }
    
    @Override
    public void initializeCaches() {
        SafeContainer.Data4Redis.cacheNameAndOopMap().keySet().forEach(name -> extRedisCaches.add(createRedisCache(name, cacheConfig)));
        super.initializeCaches();
    }
    
    @NonNull
    @Override
    public Collection<RedisCache> loadCaches() {
        Collection<RedisCache> redisCaches = super.loadCaches();
        Set<String> nameSet = redisCaches.stream().map(RedisCache::getName).collect(Collectors.toSet());
        extRedisCaches.forEach(redisCache -> {
            if (nameSet.contains(redisCache.getName())) {
                return;
            }
            redisCaches.add(redisCache);
        });
        return redisCaches;
    }
    
    @NonNull
    @Override
    public Collection<String> getCacheNames() {
        return SafeContainer.Data4Redis.EXT_REDIS_CONTAINER.stream()
                .flatMap(x -> Arrays.stream(x.getCacheNames()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    /**
     * ExtRedisCacheManager建造器
     * <p>
     * @see RedisCacheManager
     * @see RedisCacheManagerBuilder
     */
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static class ExtRedisCacheManagerBuilder {
        private RedisCacheWriter cacheWriter;
        private RedisCacheConfiguration cacheConfig;
        private Map<String, RedisCacheConfiguration> initialCacheMap;
        private boolean allowInFlightCacheCreation;
        
        private ExtRedisCacheManagerBuilder(RedisCacheManager redisCacheManager) {
            Pair<RedisCacheWriter, RedisCacheConfiguration> redisCacheWriterRedisCacheConfigurationPair = obtainCacheWriterAndDefaultCacheConfig(redisCacheManager);
            Pair<Map<String, RedisCacheConfiguration>, Boolean> mapBooleanPair = obtainInitialCacheConfigAndIfAllowCreation(redisCacheManager);
            this.cacheWriter = redisCacheWriterRedisCacheConfigurationPair.getLeft();
            this.cacheConfig = redisCacheWriterRedisCacheConfigurationPair.getRight();
            this.initialCacheMap = mapBooleanPair.getLeft();
            this.allowInFlightCacheCreation = BooleanUtils.isTrue(mapBooleanPair.getRight());
        }
        
        private ExtRedisCacheManagerBuilder(RedisCacheManager redisCacheManager, RedisCacheConfiguration cacheConfig) {
            Pair<RedisCacheWriter, RedisCacheConfiguration> redisCacheWriterRedisCacheConfigurationPair = obtainCacheWriterAndDefaultCacheConfig(redisCacheManager);
            Pair<Map<String, RedisCacheConfiguration>, Boolean> mapBooleanPair = obtainInitialCacheConfigAndIfAllowCreation(redisCacheManager);
            this.cacheWriter = redisCacheWriterRedisCacheConfigurationPair.getLeft();
            this.cacheConfig = cacheConfig;
            this.initialCacheMap = mapBooleanPair.getLeft();
            this.allowInFlightCacheCreation = BooleanUtils.isTrue(mapBooleanPair.getRight());
        }
        
        @SuppressWarnings({"rawtypes", "ConstantConditions"})
        public static ExtRedisCacheManagerBuilder builder(RedisTemplate redisTemplate) {
            return new ExtRedisCacheManagerBuilder(RedisCacheManager.create(redisTemplate.getConnectionFactory()));
        }
        
        @SuppressWarnings({"rawtypes", "ConstantConditions"})
        public static ExtRedisCacheManagerBuilder builder(RedisTemplate redisTemplate, RedisCacheConfiguration cacheConfig) {
            return new ExtRedisCacheManagerBuilder(RedisCacheManager.create(redisTemplate.getConnectionFactory()), cacheConfig);
        }
    
        public static ExtRedisCacheManagerBuilder builder(RedisConnectionFactory connectionFactory) {
            return new ExtRedisCacheManagerBuilder(RedisCacheManager.create(connectionFactory));
        }
        
        public static ExtRedisCacheManagerBuilder builder(RedisConnectionFactory connectionFactory, RedisCacheConfiguration cacheConfig) {
            return new ExtRedisCacheManagerBuilder(RedisCacheManager.create(connectionFactory), cacheConfig);
        }
    
        public static ExtRedisCacheManagerBuilder builder(RedisCacheManager redisCacheManager) {
            return new ExtRedisCacheManagerBuilder(redisCacheManager);
        }
        
        public static ExtRedisCacheManagerBuilder builder(RedisCacheManager redisCacheManager, RedisCacheConfiguration cacheConfig) {
            return new ExtRedisCacheManagerBuilder(redisCacheManager, cacheConfig);
        }
        
        /**
         * 定制初始化{@link this#cacheConfig} & 初始化{@link this#initialCacheMap}
         */
        public ExtRedisCacheManagerBuilder customCacheConfig(CacheProperties cacheProperties, ClassLoader classLoader) {
            if (cacheProperties == null) {
                return this;
            }
            CacheProperties.Redis redisProperties = cacheProperties.getRedis();
            // RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
            cacheConfig = cacheConfig.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
            if (redisProperties.getTimeToLive() != null) {
                cacheConfig = cacheConfig.entryTtl(redisProperties.getTimeToLive());
            }
            if (redisProperties.getKeyPrefix() != null) {
                cacheConfig = cacheConfig.prefixCacheNameWith(redisProperties.getKeyPrefix());
            }
            if (!redisProperties.isCacheNullValues()) {
                cacheConfig = cacheConfig.disableCachingNullValues();
            }
            if (!redisProperties.isUseKeyPrefix()) {
                cacheConfig = cacheConfig.disableKeyPrefix();
            }
            // cacheConfig定制完毕后再定制initialCacheMap(因为定制initialCacheMap时，需要用到定制后的cacheConfig)
            List<String> cacheNames = cacheProperties.getCacheNames();
            if (!cacheNames.isEmpty()) {
                cacheNames.stream().filter(Objects::nonNull).forEach(x -> withCacheConfiguration(x, cacheConfig));
            }
            return this;
        }
        
        public ExtRedisCacheManager build() {
            return new ExtRedisCacheManager(cacheWriter, cacheConfig, initialCacheMap, allowInFlightCacheCreation);
        }
    
        /**
         * @see RedisCacheManagerBuilder#withCacheConfiguration(java.lang.String, org.springframework.data.redis.cache.RedisCacheConfiguration)
         */
        public void withCacheConfiguration(String cacheName, RedisCacheConfiguration cacheConfiguration) {
            Assert.notNull(cacheName, "CacheName must not be null!");
            Assert.notNull(cacheConfiguration, "CacheConfiguration must not be null!");
            this.initialCacheMap.put(cacheName, cacheConfiguration);
        }
    
        /**
         * @see RedisCacheManagerBuilder#withInitialCacheConfigurations(java.util.Map)
         */
        public void withInitialCacheConfigurations(Map<String, RedisCacheConfiguration> cacheConfigurations) {
            Assert.notNull(cacheConfigurations, "CacheConfigurations must not be null!");
            cacheConfigurations.forEach((cacheName, configuration) -> Assert.notNull(configuration, String.format("RedisCacheConfiguration for cache %s must not be null!", cacheName)));
            this.initialCacheMap.putAll(cacheConfigurations);
        }
        
        /**
         * 获取redisCacheManager中的cacheWriter和defaultCacheConfig字段值
         *
         * @param redisCacheManager
         *         获取源对象redisCacheManager
         *
         * @return <ul>
         * <li>左-cacheWriter</li>
         * <li>右-defaultCacheConfig</li>
         * </ul>
         */
        private static Pair<RedisCacheWriter, RedisCacheConfiguration> obtainCacheWriterAndDefaultCacheConfig(RedisCacheManager redisCacheManager) {
            RedisCacheWriter cacheWriter;
            RedisCacheConfiguration defaultCacheConfig;
            try {
                // 字段cacheWriter
                Field cacheWriterField = RedisCacheManager.class.getDeclaredField("cacheWriter");
                boolean cacheWriterAccessible = cacheWriterField.isAccessible();
                cacheWriterField.setAccessible(true);
                cacheWriter = (RedisCacheWriter) cacheWriterField.get(redisCacheManager);
                cacheWriterField.setAccessible(cacheWriterAccessible);
                
                // 字段defaultCacheConfig
                Field cacheConfigField = RedisCacheManager.class.getDeclaredField("defaultCacheConfig");
                boolean cacheConfigAccessible = cacheConfigField.isAccessible();
                cacheConfigField.setAccessible(true);
                defaultCacheConfig = (RedisCacheConfiguration) cacheConfigField.get(redisCacheManager);
                cacheConfigField.setAccessible(cacheConfigAccessible);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return Pair.of(cacheWriter, defaultCacheConfig);
        }
        
        /**
         * 获取redisCacheManager中的initialCacheConfiguration和allowInFlightCacheCreation字段值
         *
         * @param redisCacheManager
         *         获取源对象redisCacheManager
         *
         * @return <ul>
         * <li>左-initialCacheConfiguration</li>
         * <li>右-allowInFlightCacheCreation</li>
         * </ul>
         */
        @SuppressWarnings("unchecked")
        private static Pair<Map<String, RedisCacheConfiguration>, Boolean> obtainInitialCacheConfigAndIfAllowCreation(RedisCacheManager redisCacheManager) {
            Map<String, RedisCacheConfiguration> initialCacheConfiguration;
            boolean allowInFlightCacheCreation;
            try {
                // 字段initialCacheConfiguration
                Field initialCacheConfigurationField = RedisCacheManager.class.getDeclaredField(
                        "initialCacheConfiguration");
                boolean cacheWriterAccessible = initialCacheConfigurationField.isAccessible();
                initialCacheConfigurationField.setAccessible(true);
                initialCacheConfiguration =
                        (Map<String, RedisCacheConfiguration>) initialCacheConfigurationField.get(redisCacheManager);
                initialCacheConfigurationField.setAccessible(cacheWriterAccessible);
                
                // 字段allowInFlightCacheCreation
                Field allowInFlightCacheCreationField = RedisCacheManager.class.getDeclaredField(
                        "allowInFlightCacheCreation");
                boolean cacheConfigAccessible = allowInFlightCacheCreationField.isAccessible();
                allowInFlightCacheCreationField.setAccessible(true);
                allowInFlightCacheCreation = (boolean) allowInFlightCacheCreationField.get(redisCacheManager);
                allowInFlightCacheCreationField.setAccessible(cacheConfigAccessible);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return Pair.of(initialCacheConfiguration, allowInFlightCacheCreation);
        }
    }
    
}