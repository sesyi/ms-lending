package com.qisstpay.lendingservice.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qisstpay.lendingservice.config.RedisConfig;
import com.qisstpay.lendingservice.service.TasdeeqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheHelper {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    TasdeeqService tasdeeqService;

    @Autowired
    private RedisConfig redisConfig;

    private final String AUTH_TOKEN_ID = "getLastAuthTokenId";
    private final String AUTH          = "authentication";
    private final String KEY           = "%s:%s";


    public void removeAuthTokenAndIdFromCache(Long id) {
        log.info("removeAuthTokenAndIdFromCache id: {}", id);
        if (cacheManager.getCache(AUTH).get(String.format(KEY, AUTH, id)).get().toString() != null) {
            log.info(cacheManager.getCache(AUTH).get(String.format(KEY, AUTH, id)).get().toString());
        }
        cacheManager.getCache(AUTH).evictIfPresent(String.format(KEY, AUTH, id));
        log.info(cacheManager.getCache(AUTH_TOKEN_ID).get(AUTH_TOKEN_ID).get().toString());
        cacheManager.getCache(AUTH_TOKEN_ID).evictIfPresent(AUTH_TOKEN_ID);
    }

}
