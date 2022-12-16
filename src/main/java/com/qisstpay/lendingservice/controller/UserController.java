package com.qisstpay.lendingservice.controller;

import com.qisstpay.commons.error.errortype.AuthenticationErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.request.ConfigRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.UserRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.ConfigurationService;
import com.qisstpay.lendingservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private static final String SAVE                 = "/save";
    private static final String VERIFY_KEY           = "/verify";
    private static final String UPDATE_CONFIGURATION = "/update/config";

    private static final String LENDER_CONTROLLER_CALLED = "UserController Called";
    private static final String RESPONSE                 = "Success Response: {}";

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Value("${auth.api-key.qpay}")
    private String qpayApiKey;

    @PostMapping(SAVE)
    public CustomResponse<MessageResponseDto> save(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody UserRequestDto userRequestDto) {
        log.info(LENDER_CONTROLLER_CALLED);
        log.info("Called save LenderUserRequestDto: {}", userRequestDto);
        Boolean check = ApiKeyAuth.verifyApiKey(apiKey, qpayApiKey);
        if (check.equals(Boolean.FALSE)) {
            log.info(AuthenticationErrorType.INVALID_API_KEY.getErrorMessage());
            throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
        }
        MessageResponseDto response = userService.saveUser(userRequestDto);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<MessageResponseDto>builder()
                .body(response).build();
    }

    @GetMapping(VERIFY_KEY)
    public CustomResponse<MessageResponseDto> verify(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "USER_ID") Long userId
    ) {
        log.info(LENDER_CONTROLLER_CALLED);
        log.info("Called verify USER_ID: {}", userId);
        MessageResponseDto response = userService.verifyUser(userId, apiKey);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<MessageResponseDto>builder()
                .body(response).build();
    }

    @PostMapping(UPDATE_CONFIGURATION)
    public CustomResponse<MessageResponseDto> updateConfiguration(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody ConfigRequestDto configRequestDto
    ) throws NoSuchAlgorithmException {
        log.info(LENDER_CONTROLLER_CALLED);
        log.info("Called updateConfiguration ConfigRequestDto: {}", configRequestDto);
        Boolean check = ApiKeyAuth.verifyApiKey(apiKey, qpayApiKey);
        if (check.equals(Boolean.FALSE)) {
            log.info(AuthenticationErrorType.INVALID_API_KEY.getErrorMessage());
            throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
        }
        MessageResponseDto response = configurationService.updateConfiguration(configRequestDto);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<MessageResponseDto>builder()
                .body(response).build();
    }
}
