package com.qisstpay.lendingservice.config.cache;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**/@Data
public class CacheProperties {


    @Value("${cache.timeout.tasdeeq.auth-token}")
    private String getAuthToken;

}