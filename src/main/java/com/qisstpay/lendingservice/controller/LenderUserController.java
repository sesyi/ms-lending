package com.qisstpay.lendingservice.controller;

import com.qisstpay.commons.error.errortype.AuthenticationErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.request.LenderUserRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.LenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/lender/v1")
@RequiredArgsConstructor
public class LenderUserController {

    private static final String SAVE       = "/save";
    private static final String VERIFY_KEY = "/verify";

    private final LenderService lenderService;

    @Value("${auth.api-key.qpay}")
    private String qpayApiKey;

    @PostMapping(SAVE)
    public CustomResponse<MessageResponseDto> save(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody LenderUserRequestDto lenderUserRequestDto) {
        Boolean check = ApiKeyAuth.verifyApiKey(apiKey, qpayApiKey);
        if (check.equals(Boolean.FALSE)) {
            throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
        }
        return CustomResponse.CustomResponseBuilder.<MessageResponseDto>builder()
                .body(lenderService.saveLender(lenderUserRequestDto)).build();
    }

    @GetMapping(VERIFY_KEY)
    public CustomResponse<MessageResponseDto> verify(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "USER_ID") Long userId
    ) {
        return CustomResponse.CustomResponseBuilder.<MessageResponseDto>builder()
                .body(lenderService.verifyUser(userId, apiKey)).build();
    }
}
