package com.qisstpay.lendingservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.qisstpay.lendingservice.config.cache.CacheProperties;
import com.qisstpay.lendingservice.config.cache.CacheUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.Metrics;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;


@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    private ConfigProperties propertyConfig;

    @Autowired
    RedisConfig(ConfigProperties propertyConfig) {
        this.propertyConfig = propertyConfig;
    }

    @Bean(value = "cacheUpdate")
    CacheUpdate getCacheUpdate(){
        return new CacheUpdate();
    }

    @Bean(value = "cacheProperties")
    CacheProperties getCacheProperties(){
        return new CacheProperties();
    }

    @RefreshScope
    @Bean(value = "jedisConnectionFactory")
    RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(propertyConfig.getRedisHostName(), propertyConfig.getRedisPort());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @RefreshScope
    @Bean(value = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Primary
    @RefreshScope
    @Bean(name = "redisCacheManager")
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Duration expiration = Duration.ofDays(8);
        CacheManager cacheManager = RedisCacheManager.builder(jedisConnectionFactory())
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .prefixKeysWith(propertyConfig.getRedisPrefix()).entryTtl(expiration)
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())))
                )
                .build();

        //test connection
        if(propertyConfig.getCacheIgnoreOnConnectionFailure() == null ||
                propertyConfig.getCacheIgnoreOnConnectionFailure().equals(Boolean.FALSE)){
            cacheManager.getCache("test")
                    .put("test", "test");
        }


        return cacheManager;
    }

    @Bean("objectMapper")
    public ObjectMapper objectMapper(){
        return new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//                .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .registerModule(new JSR310Module());
    }


}