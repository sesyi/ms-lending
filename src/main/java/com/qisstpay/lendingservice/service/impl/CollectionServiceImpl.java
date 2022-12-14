package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.enums.SlackTagType;
import com.qisstpay.commons.error.errortype.CommunicationErrorType;
import com.qisstpay.commons.exception.CustomException;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.Abroad.AbroadBillUpdateRequest;
import com.qisstpay.lendingservice.dto.Abroad.AbroadBillUpdateResponse;
import com.qisstpay.lendingservice.dto.Abroad.AbroadInquiryRequest;
import com.qisstpay.lendingservice.dto.Abroad.AbroadInquiryResponse;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionBillUpdateRequest;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionBillUpdateResponse;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionInquiryResponse;
import com.qisstpay.lendingservice.enums.AbroadResponseCode;
import com.qisstpay.lendingservice.service.CollectionService;
import com.qisstpay.lendingservice.utils.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class CollectionServiceImpl implements CollectionService {

    @Value("${abroad.endpoints.base-url}")
    private String abroadBaseUrl;

    @Value("${abroad.endpoints.inquiry}")
    private String billInquiryUrl;

    @Value("${abroad.endpoints.bill-update}")
    private String billUpdateUrl;

    @Value("${abroad.auth.access-key}")
    private String accessKey;

    @Value("${abroad.auth.access-key-value}")
    private String accessKeyValue;

    @Value("${environment}")
    private String environment;

    @Value("${message.slack.channel.third-party-errors}")
    private String thirdPartyErrorsSlackChannel;


    private final RestTemplate restTemplate = new RestTemplate();


    private static final String SUCCESS_STATUS_CODE = "00";

    @Override
    public EPCollectionInquiryResponse billInquiry(EPCollectionInquiryRequest epCollectionInquiryRequest) {
        log.info("Inquiry method has been invoked in CollectionServiceImpl class...");

        if (StringUtils.isBlank(epCollectionInquiryRequest.getConsumerNumber())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "consumer number is missing.");
        }

        AbroadInquiryRequest abroadInquiryRequest = AbroadInquiryRequest.builder().consumerNumber(epCollectionInquiryRequest.getConsumerNumber()).build();
        AbroadInquiryResponse abroadInquiryResponse;
        try {
            abroadInquiryResponse = abroadBillInquiryCall(abroadInquiryRequest);
        } catch (Exception e) {
            log.error("Exception Occurred in Abroad Inquiry for consumer: {}", epCollectionInquiryRequest.getConsumerNumber());
//            updateEpCallLog(savedEpLoginCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null);
//            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), abroadBaseUrl+billInquiryUrl, new HttpEntity<>(abroadInquiryRequest), environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (!abroadInquiryResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

//            //  update ep call log
//            updateEpCallLog(
//                    savedEpLoginCallLog,
//                    CallStatusType.FAILURE,
//                    epLoginResponse.getResponseCode(),
//                    epLoginResponse.getResponseMessage(),
//                    epLoginResponse.toString());
//
//            //  update lender call log
//            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);

            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getCode())
                    .responseMessage(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getDescription())
                    .build();
        }

//        //  update ep call log
//        updateEpCallLog(
//                savedEpLoginCallLog,
//                CallStatusType.SUCCESS,
//                epLoginResponse.getResponseCode(),
//                epLoginResponse.getResponseMessage(),
//                epLoginResponse.toString());

        return EPCollectionInquiryResponse
                .builder()
                .responseCode(abroadInquiryResponse.getResponseCode())
                .amountAfterDueDate(abroadInquiryResponse.getAmountAfterDueDate())
                .amountPaid(abroadInquiryResponse.getAmountPaid())
                .amountWithinDueDate(abroadInquiryResponse.getAmountWithinDueDate())
                .billingMonth(abroadInquiryResponse.getBillingMonth())
                .billStatus(abroadInquiryResponse.getBillStatus())
                .consumerName(abroadInquiryResponse.getConsumerName())
                .datePaid(abroadInquiryResponse.getDatePaid())
                .dueDate(abroadInquiryResponse.getDueDate())
                .build();
    }

    @Override
    public EPCollectionBillUpdateResponse billUpdate(EPCollectionBillUpdateRequest epCollectionBillUpdateRequest) {
        log.info("Update method has been invoked in CollectionServiceImpl class...");

        if (StringUtils.isBlank(epCollectionBillUpdateRequest.getConsumerNumber())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "consumer number is missing.");
        }

        AbroadBillUpdateRequest abroadBillUpdateRequest = AbroadBillUpdateRequest.builder().consumerNumber(epCollectionBillUpdateRequest.getConsumerNumber()).build();
        AbroadBillUpdateResponse abroadBillUpdateResponse;
        try {
            abroadBillUpdateResponse = abroadBillUpdateCall(abroadBillUpdateRequest);
        } catch (Exception e) {
            log.error("Exception Occurred in Abroad Bill Update for consumer: {}", epCollectionBillUpdateRequest.getConsumerNumber());
//            updateEpCallLog(savedEpLoginCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null);
//            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), abroadBaseUrl+billUpdateUrl, new HttpEntity<>(abroadBillUpdateRequest), environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (!abroadBillUpdateResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

//            //  update ep call log
//            updateEpCallLog(
//                    savedEpLoginCallLog,
//                    CallStatusType.FAILURE,
//                    epLoginResponse.getResponseCode(),
//                    epLoginResponse.getResponseMessage(),
//                    epLoginResponse.toString());
//
//            //  update lender call log
//            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);

            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getCode())
                    .responseMessage(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getDescription())
                    .build();
        }

//        //  update ep call log
//        updateEpCallLog(
//                savedEpLoginCallLog,
//                CallStatusType.SUCCESS,
//                epLoginResponse.getResponseCode(),
//                epLoginResponse.getResponseMessage(),
//                epLoginResponse.toString());

        return EPCollectionBillUpdateResponse
                .builder()
                .responseCode(abroadBillUpdateResponse.getResponseCode())
                .identificationParameter(abroadBillUpdateResponse.getIdentificationParameter())
                .tranAuthId("TO BE ADDED")
                .build();
    }

    public AbroadInquiryResponse abroadBillInquiryCall(AbroadInquiryRequest abroadInquiryRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(accessKey, accessKeyValue);
        HttpEntity request = new HttpEntity(abroadInquiryRequest, headers);

        log.info("Abroad Bill Inquiry Request: {}", CommonUtility.getObjectJson(request));
        ResponseEntity<AbroadInquiryResponse> abroadInquiryResponse =
                restTemplate.exchange(abroadBaseUrl + billInquiryUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                });
        return abroadInquiryResponse.getBody();
    }

    public AbroadBillUpdateResponse abroadBillUpdateCall(AbroadBillUpdateRequest abroadBillUpdateRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(accessKey, accessKeyValue);
        HttpEntity request = new HttpEntity(abroadBillUpdateRequest, headers);

        log.info("Abroad Bill Update Request: {}", CommonUtility.getObjectJson(request));
        ResponseEntity<AbroadBillUpdateResponse> abroadBillUpdateResponse =
                restTemplate.exchange(abroadBaseUrl + billUpdateUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                });
        return abroadBillUpdateResponse.getBody();
    }

}
