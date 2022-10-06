package com.qisstpay.lendingservice.config.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CacheEntry<T> {
    private Object object;
    private Class listObjectClass;
    private LocalDateTime expiresAt;
}