package com.qisstpay.lendingservice.controller;

import com.qisstpay.commons.error.errortype.AuthenticationErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionBillUpdateRequest;
import com.qisstpay.lendingservice.dto.internal.request.CollectionBillRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.QpayCollectionRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CollectionBillResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayCollectionResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayLinkResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.CallType;
import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.security.MFBUserAuth;
import com.qisstpay.lendingservice.service.CollectionService;
import com.qisstpay.lendingservice.service.CollectionTransactionService;
import com.qisstpay.lendingservice.service.LendingCallService;
import com.qisstpay.lendingservice.service.UserService;
import com.qisstpay.lendingservice.utils.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/collection/v1")
@RequiredArgsConstructor
@CrossOrigin
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private CollectionTransactionService collectionTransactionService;

    @Autowired
    private TokenParser tokenParser;

    @Autowired
    private UserService userService;

    @Autowired
    private LendingCallService lendingCallService;

    @Autowired
    private MFBUserAuth mfbUserAuth;

    @Value("${auth.api-key.qpay}")
    private String qpayApiKey;

    private static final String QPAY                       = "/qpay";
    private static final String GET_QPAY_COLLECTION_STATUS = "/qpay/status";
    private static final String QPAY_CALLBACK_STATUS = "/qpay/callback";
    private static final String GET_QPAY_LINK              = "/qpay/link";
    private static final String GET_BILL                   = "/get/bill";
    private static final String TEST                       = "/test";

    private static final String CALLING_CONTROLLER            = "Calling CollectionController";
    private static final String CALLING_COLLECTION_CONTROLLER = "Calling Collection Controller";
    private static final String RESPONSE                      = "Success Response: {}";


    @PostMapping(GET_QPAY_LINK)
    public CustomResponse<QpayLinkResponseDto> getQpayLink(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestBody CollectionBillRequestDto billRequestDto
    ) {
        log.info(CALLING_CONTROLLER);
        log.info("In method" + GET_QPAY_LINK + " with request {}", billRequestDto);
        Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
        Optional<User> lender = userService.getUserByUsername(userId);
        ApiKeyAuth.verifyApiKey(lender, apiKey);

        log.info("adding call log for lender {}", lender.get().getId());
        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(lender.get(), billRequestDto.toString(), ServiceType.QPAY, CallType.RECEIVED);

        QpayLinkResponseDto response = collectionService.getQpayLink(billRequestDto, lenderCallLog);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<QpayLinkResponseDto>builder()
                .body(response).build();
    }

    @GetMapping(GET_BILL)
    public CustomResponse<CollectionBillResponseDto> getCollectionBill(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam Long billId
    ) {
        log.info(CALLING_CONTROLLER);
        log.info("In method" + GET_BILL + " with billId {}", billId);
        if (authorizationHeader != null) {
            Long userId = tokenParser.getUserIdFromToken(authorizationHeader);
            Optional<User> lender = userService.getUserByUsername(userId);
            ApiKeyAuth.verifyApiKey(lender, apiKey);
        } else {
            Boolean check = ApiKeyAuth.verifyApiKey(apiKey, qpayApiKey);
            if (check.equals(Boolean.FALSE)) {
                log.info(AuthenticationErrorType.INVALID_API_KEY.getErrorMessage());
                throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
            }
        }

        CollectionBillResponseDto response = collectionTransactionService.geBill(billId);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<CollectionBillResponseDto>builder()
                .body(response).build();
    }

    @PostMapping(QPAY)
    public CustomResponse<QpayCollectionResponseDto> pay(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody QpayCollectionRequestDto collectionRequestDto
    ) {
        log.info(CALLING_CONTROLLER);
        log.info("In method" + QPAY + " with request {}", collectionRequestDto);
        Boolean check = ApiKeyAuth.verifyApiKey(apiKey, qpayApiKey);
        if (check.equals(Boolean.FALSE)) {
            log.info(AuthenticationErrorType.INVALID_API_KEY.getErrorMessage());
            throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
        }

        LenderCallLog callLog = lendingCallService.saveLenderCall(collectionRequestDto.toString(), ServiceType.QPAY, CallType.RECEIVED);

        QpayCollectionResponseDto response = collectionService.collectTroughQpay(collectionRequestDto, callLog);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<QpayCollectionResponseDto>builder()
                .body(response).build();
    }

    @PostMapping(GET_QPAY_COLLECTION_STATUS)
    public CustomResponse<QpayCollectionResponseDto> getQpayCollectionStatus(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestParam Long billId,
            @RequestParam(required = false) String otp
    ) {
        log.info(CALLING_CONTROLLER);
        log.info("In method" + GET_QPAY_COLLECTION_STATUS + " with billId: {}", billId);
        Boolean check = ApiKeyAuth.verifyApiKey(apiKey, qpayApiKey);
        if (check.equals(Boolean.FALSE)) {
            log.info(AuthenticationErrorType.INVALID_API_KEY.getErrorMessage());
            throw new ServiceException(AuthenticationErrorType.INVALID_API_KEY);
        }

        LenderCallLog callLog = lendingCallService.saveLenderCall(String.format("billId: %s", billId), ServiceType.QPAY, CallType.RECEIVED);

        QpayCollectionResponseDto response = collectionService.qpayCollectionStatus(billId, callLog, otp);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<QpayCollectionResponseDto>builder()
                .body(response).build();
    }

    @PostMapping(QPAY_CALLBACK_STATUS)
    public CustomResponse<QpayCollectionResponseDto> getQpayCallbackStatus(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestParam(value = "order_id") String orderId,
            @RequestParam(value = "transaction_id") String transactionId,
            @RequestParam(value = "result") String result
    ) {
        log.info(CALLING_CONTROLLER);
        log.info("In method" + GET_QPAY_COLLECTION_STATUS + " with orderId: {} transactionId: {} result: {}", orderId, transactionId, result);

        LenderCallLog callLog = lendingCallService.saveLenderCall(String.format("orderId: %s transactionId: %s result: %s", orderId, transactionId, result), ServiceType.QPAY, CallType.RECEIVED);

        QpayCollectionResponseDto response = collectionService.qpayCallbackStatus(orderId, transactionId, result, callLog);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<QpayCollectionResponseDto>builder()
                .body(response).build();
    }

    @PostMapping(TEST)
    public CustomResponse<MessageResponseDto> test(
            @RequestBody EPCollectionBillUpdateRequest epCollectionBillUpdateRequest) {
        log.info(CALLING_COLLECTION_CONTROLLER);
        // mfb authentication
        Optional<User> user = userService.getUserByUsername(epCollectionBillUpdateRequest.getUsername());
        mfbUserAuth.verifyUser(epCollectionBillUpdateRequest.getUsername(), epCollectionBillUpdateRequest.getPassword());
        log.info("adding call log for lender {}", user.get().getId());
//        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(user.get(), transferRequestDto.toString(), transferRequestDto.getType() == TransferType.HMB? ServiceType.HMB: ServiceType.EP);
        return CustomResponse.CustomResponseBuilder.<MessageResponseDto>builder().body(MessageResponseDto.builder().message("Test successful").build()).build();
    }
}