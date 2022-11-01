package com.qisstpay.lendingservice.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuth {

    public static Boolean verifyApiKey(final String apiKey, final String encodedKey) {
        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
        return bc.matches(apiKey,encodedKey);
    }

}
