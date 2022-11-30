package com.qisstpay.lendingservice.config.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Component
@Slf4j
public class CacheHelper {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CacheManager cacheManager;

    private final String AUTH_TOKEN_ID = "getLastAuthTokenId";
    private final String AUTH          = "authentication";
    private final String KEY           = "%s:%s";


    public void removeAuthTokenAndIdFromCache(Long id) {
        log.info("removeAuthTokenAndIdFromCache id: {}", id);
        if (cacheManager.getCache(AUTH).get(String.format(KEY, AUTH, id)) != null) {
            log.info(cacheManager.getCache(AUTH).get(String.format(KEY, AUTH, id)).get().toString());
        }
        cacheManager.getCache(AUTH).evictIfPresent(String.format(KEY, AUTH, id));
        if (cacheManager.getCache(AUTH_TOKEN_ID).get(AUTH_TOKEN_ID) != null) {
            log.info(cacheManager.getCache(AUTH_TOKEN_ID).get(AUTH_TOKEN_ID).get().toString());
        }
        cacheManager.getCache(AUTH_TOKEN_ID).evictIfPresent(AUTH_TOKEN_ID);
    }


    public void addAuthIdToCache(final Long authId) {
        LocalDateTime timeStamp = LocalDateTime.now();
        Cache cache = cacheManager.getCache(AUTH_TOKEN_ID);
        cache.put(AUTH_TOKEN_ID, CacheEntry.builder().object(authId).listObjectClass(null).expiresAt(LocalDateTime.now().plus(60000L, ChronoUnit.MILLIS)).build());
    }
}
