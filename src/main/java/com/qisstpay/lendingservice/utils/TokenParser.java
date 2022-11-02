package com.qisstpay.lendingservice.utils;

import com.qisstpay.commons.error.errortype.UserErrorType;
import com.qisstpay.commons.exception.ServiceException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class TokenParser {

    public Long getUserIdFromToken(String token) {
        String[] jwtSplitted = token.split("\\.");
        try {
            String jsonSecondPart = new String(Base64.getDecoder().decode(jwtSplitted[1]));
            JSONObject secondPart = new JSONObject(jsonSecondPart);
            if (secondPart.has("user_id")) {
                return Long.parseLong(secondPart.get("user_id").toString());
            } else {
                throw new ServiceException(UserErrorType.USER_NOT_FOUND);
            }
        } catch (JSONException err) {
            throw new ServiceException(UserErrorType.USER_NOT_FOUND);
        }
    }
}
