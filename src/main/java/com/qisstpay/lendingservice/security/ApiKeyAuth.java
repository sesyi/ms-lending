package com.qisstpay.lendingservice.security;

import com.qisstpay.commons.error.errortype.AuthenticationErrorType;
import com.qisstpay.commons.error.errortype.UserErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.StatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ApiKeyAuth {

    public static Boolean verifyApiKey(final String apiKey, final String encodedKey) {
        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
        return bc.matches(apiKey,encodedKey);
    }

    public static void verifyApiKey(Optional<User> user, String apiKey) {
        if (user.isPresent()) {
            if (user.get().getStatus().equals(StatusType.BLOCKED)) {
                log.error(UserErrorType.LENDER_BLOCKED.getErrorMessage());
                throw new ServiceException(UserErrorType.LENDER_BLOCKED);
            }
            if (!BCrypt.checkpw(apiKey, user.get().getApiKey())) {
                log.error(AuthenticationErrorType.INVALID_API_KEY.getErrorMessage());
                throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
            }
        } else {
            log.error(AuthenticationErrorType.INVALID_TOKEN.getErrorMessage());
            throw new ServiceException(AuthenticationErrorType.INVALID_TOKEN);
        }
    }

}
