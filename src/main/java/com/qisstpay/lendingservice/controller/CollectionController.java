package com.qisstpay.lendingservice.controller;

import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
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
    private static final String INQUIRY = "/inquiry";

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
            @RequestBody EPCollectionInquiryRequest epCollectionInquiryRequest) {
        log.info(CALLING_COLLECTION_CONTROLLER);

        //TODO: mfb auth

        return CustomResponse.CustomResponseBuilder.<EPCollectionInquiryResponse>builder().body(collectionService.inquiry(epCollectionInquiryRequest)).build();
    }
}
