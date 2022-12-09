//package com.qisstpay.lendingservice.service.impl;
//
//import com.qisstpay.commons.enums.SlackTagType;
//import com.qisstpay.commons.error.errortype.CommunicationErrorType;
//import com.qisstpay.commons.exception.ServiceException;
//import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
//import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionInquiryResponse;
//import com.qisstpay.lendingservice.dto.easypaisa.response.EPLoginResponseDto;
//import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
//import com.qisstpay.lendingservice.enums.CallStatusType;
//import com.qisstpay.lendingservice.enums.QPResponseCode;
//import com.qisstpay.lendingservice.service.CollectionService;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CollectionServiceImpl implements CollectionService {
//
//    @Override
//    public EPCollectionInquiryResponse inquiry(EPCollectionInquiryRequest epCollectionInquiryRequest) {
//        EPLoginResponseDto epLoginResponse;
//        try {
//            epLoginResponse = epLogin(epLoginRequestDto);
//        } catch (Exception e) {
//            log.error("Exception Occurred in EP Login for consumer: {}", transferRequestDto.getPhoneNumber());
//            updateEpCallLog(savedEpLoginCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null);
//            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);
//            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), epLoginUrl, epLoginRequestDto, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
//        }
//        if (!epLoginResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {
//
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
//
//            return TransferResponseDto
//                    .builder()
//                    .qpResponseCode(QPResponseCode.EP_LOGIN_FAILED.getCode())
//                    .result(QPResponseCode.EP_LOGIN_FAILED.getDescription())
////                    .epResult(epLoginResponse)
//                    .build();
//        }
//
//        //  update ep call log
//        updateEpCallLog(
//                savedEpLoginCallLog,
//                CallStatusType.SUCCESS,
//                epLoginResponse.getResponseCode(),
//                epLoginResponse.getResponseMessage(),
//                epLoginResponse.toString());
//    }
//
//}
