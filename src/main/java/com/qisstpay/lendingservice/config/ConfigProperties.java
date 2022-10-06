package com.qisstpay.lendingservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
public class ConfigProperties {


    @Value("${cache.redis.hostname}")
    private String redisHostName;

    @Value("${cache.redis.port}")
    private Integer redisPort;

    @Value("${cache.redis.prefix}")
    private String redisPrefix;

    @Value("${cache.redis.ignoreOnConnectionFailure}")
    private Boolean cacheIgnoreOnConnectionFailure;
}
