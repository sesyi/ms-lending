package com.qisstpay.lendingservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.commons.error.errortype.AuthenticationErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.Lender;
import com.qisstpay.lendingservice.repository.LenderRepository;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.LenderService;
import com.qisstpay.lendingservice.service.LendingService;
import com.qisstpay.lendingservice.utils.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/lending/v1")
@RequiredArgsConstructor
public class LendingController {


    private static final String TRANSFER     = "/transfer";
    private static final String STATUS       = "/status/{transactionId}";
    private static final String CREDIT_SCORE = "/score";

    private final LendingService lendingService;
    private final TokenParser    tokenParser;
    private final LenderService  lenderService;

    @Autowired
    private LenderRepository lenderRepository;

    @PostMapping(TRANSFER)
    public CustomResponse<TransferResponseDto> transfer(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody TransferRequestDto transferRequestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) throws JsonProcessingException {
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Lender lender = lenderService.getLender(userId);
        Boolean check = ApiKeyAuth.verifyApiKey(apiKey, lender.getApiKey());
        if (check.equals(Boolean.FALSE)) {
            throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
        }
        return CustomResponse.CustomResponseBuilder.<TransferResponseDto>builder()
                .body(lendingService.transfer(transferRequestDto)).build();
    }

    @GetMapping(STATUS)
    public CustomResponse<TransactionStateResponse> status(
            @RequestHeader(value = "x-api-key") String apiKey,
            @PathVariable("transactionId") String transactionId,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Lender lender = lenderService.getLender(userId);
        Boolean check = ApiKeyAuth.verifyApiKey(apiKey, lender.getApiKey());
        if (check.equals(Boolean.FALSE)) {
            throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
        }
        return CustomResponse.CustomResponseBuilder.<TransactionStateResponse>builder()
                .body(lendingService.checkStatus(transactionId)).build();
    }

    @PostMapping(CREDIT_SCORE)
    public CustomResponse<TransferResponseDto> getScore(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody TransferRequestDto transferRequestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Lender lender = lenderService.getLender(userId);
        Boolean check = ApiKeyAuth.verifyApiKey(apiKey, lender.getApiKey());
        if (check.equals(Boolean.FALSE)) {
            throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
        }
        return CustomResponse.CustomResponseBuilder.<TransferResponseDto>builder()
                .body(lendingService.checkCredirScore(transferRequestDto)).build();
    }
}
