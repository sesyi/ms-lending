package com.qisstpay.lendingservice.controller;


import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.response.GetBanksListResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.BankService;
import com.qisstpay.lendingservice.service.UserService;
import com.qisstpay.lendingservice.utils.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/banks/v1")
@RequiredArgsConstructor
public class BankController {

    private static final String GET_ALL = "/all";

    @Autowired
    private TokenParser tokenParser;

    @Autowired
    private UserService userService;

    @Autowired
    private BankService bankService;


    @GetMapping(GET_ALL)
    public CustomResponse<GetBanksListResponseDto> status(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<User> user = userService.getUser(userId);
        ApiKeyAuth.verifyApiKey(user, apiKey);

        return CustomResponse.CustomResponseBuilder.<GetBanksListResponseDto>builder().body(
                bankService.getBanks()
        ).build();
    }

}
