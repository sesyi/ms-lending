package com.qisstpay.lendingservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.commons.error.errortype.AuthenticationErrorType;
import com.qisstpay.commons.error.errortype.UserErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CreditScoreResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.Lender;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.enums.StatusType;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.LenderService;
import com.qisstpay.lendingservice.service.LendingCallService;
import com.qisstpay.lendingservice.service.LendingService;
import com.qisstpay.lendingservice.utils.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/lending/v1")
@RequiredArgsConstructor
public class LendingController {

    @Autowired
    private LendingService lendingService;

    @Autowired
    private TokenParser tokenParser;

    @Autowired
    private LenderService lenderService;

    @Autowired
    private LendingCallService lendingCallService;

    private static final String TRANSFER     = "/transfer";
    private static final String STATUS       = "/status/{transactionId}";
    private static final String CREDIT_SCORE = "/credit/score";


    private static final String CALLING_LENDING_CONTROLLER = "Calling LendingController";
    private static final String RESPONSE                   = "Success Response: {}";


    @PostMapping(TRANSFER)
    public CustomResponse<TransferResponseDto> transfer(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody TransferRequestDto transferRequestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) throws JsonProcessingException {
        log.info(CALLING_LENDING_CONTROLLER);
        log.info("In method" + TRANSFER + " with request {}", transferRequestDto);
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<Lender> lender = lenderService.getLender(userId);
        if (lender.isPresent()) {
            if (lender.get().getStatus().equals(StatusType.BLOCKED)) {
                throw new ServiceException(UserErrorType.LENDER_BLOCKED);
            }
            Boolean check = ApiKeyAuth.verifyApiKey(apiKey, lender.get().getApiKey());
            if (check.equals(Boolean.FALSE)) {
                throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
            }
        } else {
            throw new ServiceException(AuthenticationErrorType.INVALID_TOKEN);
        }
        log.info("adding call log for lender {}", lender.get().getId());
        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(lender.get(), transferRequestDto.toString(), ServiceType.EP);

        return CustomResponse.CustomResponseBuilder.<TransferResponseDto>builder()
                .body(lendingService.transfer(transferRequestDto, lenderCallLog)).build();
    }

    @GetMapping(STATUS)
    public CustomResponse<TransactionStateResponse> status(
            @RequestHeader(value = "x-api-key") String apiKey,
            @PathVariable("transactionId") String transactionId,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<Lender> lender = lenderService.getLender(userId);
        if (lender.isPresent()) {
            if (lender.get().getStatus().equals(StatusType.BLOCKED)) {
                throw new ServiceException(UserErrorType.LENDER_BLOCKED);
            }
            Boolean check = ApiKeyAuth.verifyApiKey(apiKey, lender.get().getApiKey());
            if (check.equals(Boolean.FALSE)) {
                throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
            }
        } else {
            throw new ServiceException(AuthenticationErrorType.INVALID_TOKEN);
        }

        log.info("adding call log for lender {}", lender.get().getId());
        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(lender.get(), transactionId, ServiceType.TRXN_STATE_CHECK);

        return CustomResponse.CustomResponseBuilder.<TransactionStateResponse>builder()
                .body(lendingService.checkStatus(transactionId, lenderCallLog)).build();
    }

    @PostMapping(CREDIT_SCORE)
    public CustomResponse<CreditScoreResponseDto> getScore(
            @RequestHeader(value = "x-api-key") String apiKey,
            @Valid @RequestBody CreditScoreRequestDto creditScoreRequestDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) throws JsonProcessingException {
        log.info(CALLING_LENDING_CONTROLLER);
        log.info("getScore creditScoreRequestDto: {}", creditScoreRequestDto);
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<Lender> lender = lenderService.getLender(userId);
        if (lender.isPresent()) {
            if (lender.get().getStatus().equals(StatusType.BLOCKED)) {
                throw new ServiceException(UserErrorType.LENDER_BLOCKED);
            }
            Boolean check = ApiKeyAuth.verifyApiKey(apiKey, lender.get().getApiKey());
            if (check.equals(Boolean.FALSE)) {
                log.error(AuthenticationErrorType.INVALID_API_KEY.getErrorMessage());
                throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
            }
        } else {
            log.error(AuthenticationErrorType.INVALID_TOKEN.getErrorMessage());
            throw new ServiceException(AuthenticationErrorType.INVALID_TOKEN);
        }
        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(lender.get(), creditScoreRequestDto.toString(), ServiceType.TASDEEQ);
        CreditScoreResponseDto response = lendingService.checkCreditScore(creditScoreRequestDto, lenderCallLog.getId());
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<CreditScoreResponseDto>builder()
                .body(response).build();

    }

}
