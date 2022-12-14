package com.qisstpay.lendingservice.controller;

import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionBillUpdateRequest;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionBillUpdateResponse;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionInquiryResponse;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.CollectionService;
import com.qisstpay.lendingservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private CollectionService collectionService;

    private static final String TEST = "/test";
    private static final String INQUIRY = "bill/inquiry";
    private static final String UPDATE = "bill/update";

    private static final String CALLING_COLLECTION_CONTROLLER = "Calling Collection Controller";
    private static final String RESPONSE                   = "Success Response: {}";


    @PostMapping(TEST)
    public CustomResponse<MessageResponseDto> test(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "user-name") String userName
    ) throws NoSuchAlgorithmException {
        log.info(CALLING_COLLECTION_CONTROLLER);
        Optional<User> user = userService.getUser(userName);
        ApiKeyAuth.verifyApiKey(user, apiKey);
        MessageResponseDto response = new MessageResponseDto("Test Successful", Boolean.TRUE);
        log.info(RESPONSE, response);
        return CustomResponse.CustomResponseBuilder.<MessageResponseDto>builder()
                .body(response).build();
    }

    @PostMapping(INQUIRY)
    public CustomResponse<EPCollectionInquiryResponse> inquiry(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "user-name") String userName,
            @RequestBody EPCollectionInquiryRequest epCollectionInquiryRequest) {
        log.info(CALLING_COLLECTION_CONTROLLER);

        // mfb authentication
        Optional<User> user = userService.getUser(userName);
        ApiKeyAuth.verifyApiKey(user, apiKey);

//        log.info("adding call log for lender {}", user.get().getId());
//        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(user.get(), transferRequestDto.toString(), transferRequestDto.getType() == TransferType.HMB? ServiceType.HMB: ServiceType.EP);

        return CustomResponse.CustomResponseBuilder.<EPCollectionInquiryResponse>builder().body(collectionService.billInquiry(epCollectionInquiryRequest)).build();
    }

    @PostMapping(UPDATE)
    public CustomResponse<EPCollectionBillUpdateResponse> billUpdate(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestHeader(value = "user-name") String userName,
            @RequestBody EPCollectionBillUpdateRequest epCollectionBillUpdateRequest) {
        log.info(CALLING_COLLECTION_CONTROLLER);

        // mfb authentication
        Optional<User> user = userService.getUser(userName);
        ApiKeyAuth.verifyApiKey(user, apiKey);

//        log.info("adding call log for lender {}", user.get().getId());
//        LenderCallLog lenderCallLog = lendingCallService.saveLenderCall(user.get(), transferRequestDto.toString(), transferRequestDto.getType() == TransferType.HMB? ServiceType.HMB: ServiceType.EP);

        return CustomResponse.CustomResponseBuilder.<EPCollectionBillUpdateResponse>builder().body(collectionService.billUpdate(epCollectionBillUpdateRequest)).build();
    }

}
