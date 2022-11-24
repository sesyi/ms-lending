package com.qisstpay.lendingservice.controller;

import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/collection/v1")
@RequiredArgsConstructor
public class CollectionController {

    @Autowired
    private UserService userService;

    private static final String TEST = "/test";

    private static final String CALLING_LENDING_CONTROLLER = "Calling LendingController";
    private static final String RESPONSE                   = "Success Response: {}";


    @PostMapping(TEST)
    public CustomResponse<MessageResponseDto> test(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "user-name") String userName
    ) throws NoSuchAlgorithmException {
        log.info(CALLING_LENDING_CONTROLLER);
        Optional<User> user = userService.getUser(userName);
        ApiKeyAuth.verifyApiKey(user, apiKey);
        MessageResponseDto response = new MessageResponseDto("Test Successful", Boolean.TRUE);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<MessageResponseDto>builder()
                .body(response).build();
    }
}
