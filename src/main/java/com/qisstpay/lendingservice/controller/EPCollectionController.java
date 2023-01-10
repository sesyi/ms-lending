package com.qisstpay.lendingservice.controller;

import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionBillUpdateRequest;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionBillUpdateResponse;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionInquiryResponse;
import com.qisstpay.lendingservice.entity.EPCallLog;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.CallType;
import com.qisstpay.lendingservice.enums.EndPointType;
import com.qisstpay.lendingservice.enums.TransferState;
import com.qisstpay.lendingservice.security.MFBUserAuth;
import com.qisstpay.lendingservice.service.CollectionService;
import com.qisstpay.lendingservice.service.LendingService;
import com.qisstpay.lendingservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/1.0/Payments")
@RequiredArgsConstructor
@CrossOrigin
public class EPCollectionController {

    private static final String INQUIRY                    = "/BillInquiry";
    private static final String UPDATE                     = "/BillPayment";

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private LendingService lendingService;

    @Autowired
    private MFBUserAuth mfbUserAuth;

    private static final String CALLING_COLLECTION_CONTROLLER = "Calling Collection Controller";



    @PostMapping(INQUIRY)
    public EPCollectionInquiryResponse inquiry(
            @RequestBody EPCollectionInquiryRequest epCollectionInquiryRequest) throws ParseException {
        log.info(CALLING_COLLECTION_CONTROLLER);

        // mfb(3rd-party) authentication
        EPCollectionInquiryResponse epCollectionInquiryResponse = new EPCollectionInquiryResponse();
        Optional<User> user = userService.getUserByUsername(epCollectionInquiryRequest.getUsername());
        if ( !mfbUserAuth.isUserVerified(epCollectionInquiryRequest.getUsername(), epCollectionInquiryRequest.getPassword(), epCollectionInquiryResponse) ) {
            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(TransferState.INVALID_DATA_EP.getCode())
                    .responseMessage(TransferState.INVALID_DATA_EP.getState())
                    .status(TransferState.INVALID_DATA_EP.getDescription())
                    .build();
        }

        // add call logs
        log.info("adding call log for mfb user: {}, lender UCID: {}", user.get().getId(), epCollectionInquiryRequest.getBankMnemonic());
        EPCallLog savedEpLoginCallLog = lendingService.addEPCalLog(
                EndPointType.BILL_INQUIRY,
                epCollectionInquiryRequest.toString(),
                null,
                CallType.RECEIVED,
                user.get());

        return collectionService.billInquiry(epCollectionInquiryRequest, savedEpLoginCallLog);
    }

    @PostMapping(UPDATE)
    public EPCollectionBillUpdateResponse billUpdate(
            @RequestBody EPCollectionBillUpdateRequest epCollectionBillUpdateRequest) throws ParseException {
        log.info(CALLING_COLLECTION_CONTROLLER);
        // mfb authentication
        EPCollectionInquiryResponse epCollectionInquiryResponse = new EPCollectionInquiryResponse();
        Optional<User> user = userService.getUserByUsername(epCollectionBillUpdateRequest.getUsername());
        if ( !mfbUserAuth.isUserVerified(epCollectionBillUpdateRequest.getUsername(), epCollectionBillUpdateRequest.getPassword(), epCollectionInquiryResponse) ) {
            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(TransferState.INVALID_DATA_EP.getCode())
                    .identificationParameter(TransferState.INVALID_DATA_EP.getState())
                    .reserved(TransferState.INVALID_DATA_EP.getDescription())
                    .build();
        }
        // add call logs
        log.info("adding call log for mfb user: {}, lender UCID: {}", user.get().getId(), epCollectionBillUpdateRequest.getBankMnemonic());
        EPCallLog savedEpLoginCallLog = lendingService.addEPCalLog(
                EndPointType.BILL_UPDATE,
                epCollectionBillUpdateRequest.toString(),
                null,
                CallType.RECEIVED,
                user.get());

        return collectionService.billUpdate(epCollectionBillUpdateRequest, savedEpLoginCallLog);
    }
}