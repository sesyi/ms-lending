package com.qisstpay.lendingservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.FetchTitleRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CreditScoreResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.FetchTitleResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.CallType;
import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.enums.TransferType;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.LendingCallService;
import com.qisstpay.lendingservice.service.LendingService;
import com.qisstpay.lendingservice.service.UserService;
import com.qisstpay.lendingservice.utils.TokenParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/lending/v1")
public class LendingController {

    @Autowired
    private LendingService lendingService;

    @Autowired
    private TokenParser tokenParser;

    @Autowired
    private UserService userService;

    @Autowired
    private LendingCallService lendingCallService;

    private static final String FETCH_ACCOUNT_TITLE     = "/titlefetch";
    private static final String TRANSFER                = "/transfer";
    private static final String STATUS                  = "/status/{transactionId}";
    private static final String CREDIT_SCORE            = "/credit/score";

    private static final String CALLING_LENDING_CONTROLLER = "Calling LendingController";
    private static final String RESPONSE                   = "Success Response: {}";

    @PostMapping(FETCH_ACCOUNT_TITLE)
    public CustomResponse<FetchTitleResponseDto> fetchTitle(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody FetchTitleRequestDto fetchTitleRequestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) throws JsonProcessingException {
        log.info(CALLING_LENDING_CONTROLLER);
        log.info("In method" + TRANSFER + " with request {}", fetchTitleRequestDto);
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<User> user = userService.getUserByUsername(userId);
        ApiKeyAuth.verifyApiKey(user, apiKey);

        log.info("adding call log for lender {}", user.get().getId());
        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(user.get(), fetchTitleRequestDto.toString(), fetchTitleRequestDto.getType() == TransferType.HMB ? ServiceType.HMB : ServiceType.EP, CallType.RECEIVED);

        return CustomResponse.CustomResponseBuilder.<FetchTitleResponseDto>builder()
                .body(lendingService.fetchTitle(fetchTitleRequestDto, lenderCallLog)).build();
    }

    @PostMapping(TRANSFER)
    public CustomResponse<TransferResponseDto> transfer(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody TransferRequestDto transferRequestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) throws JsonProcessingException {
        log.info(CALLING_LENDING_CONTROLLER);
        log.info("In method" + TRANSFER + " with request {}", transferRequestDto);
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<User> user = userService.getUserByUsername(userId);
        ApiKeyAuth.verifyApiKey(user, apiKey);

        log.info("adding call log for lender {}", user.get().getId());
        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(user.get(), transferRequestDto.toString(), transferRequestDto.getType() == TransferType.HMB ? ServiceType.HMB : ServiceType.EP, CallType.RECEIVED);

        return CustomResponse.CustomResponseBuilder.<TransferResponseDto>builder()
                .body(lendingService.transfer(transferRequestDto, lenderCallLog)).build();
    }

    @GetMapping(STATUS)
    public CustomResponse<TransactionStateResponse> status(
            @RequestHeader(value = "x-api-key") String apiKey,
            @PathVariable("transactionId") String transactionStamp,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<User> user = userService.getUserByUsername(userId);
        ApiKeyAuth.verifyApiKey(user, apiKey);

        log.info("adding call log for lender {}", user.get().getId());
        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(user.get(), transactionStamp, ServiceType.TRXN_STATE_CHECK, CallType.RECEIVED);

        return CustomResponse.CustomResponseBuilder.<TransactionStateResponse>builder()
                .body(lendingService.checkStatus(transactionStamp, lenderCallLog)).build();
    }

    @PostMapping(CREDIT_SCORE)
    public CustomResponse<CreditScoreResponseDto> getScore(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody CreditScoreRequestDto creditScoreRequestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) throws JsonProcessingException {
        log.info(CALLING_LENDING_CONTROLLER);
        log.info("getScore creditScoreRequestDto: {}", creditScoreRequestDto);
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<User> user = userService.getUserByUsername(userId);
        ApiKeyAuth.verifyApiKey(user, apiKey);
        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(user.get(), creditScoreRequestDto.toString(), ServiceType.TASDEEQ, CallType.RECEIVED);
        CreditScoreResponseDto response = lendingService.checkCreditScore(creditScoreRequestDto, lenderCallLog);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<CreditScoreResponseDto>builder()
                .body(response).build();
    }


}
