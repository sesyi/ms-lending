package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionBillUpdateRequest;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionBillUpdateResponse;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionInquiryResponse;
import com.qisstpay.lendingservice.dto.internal.request.CollectionBillRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.QpayCollectionRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayCollectionResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayLinkResponseDto;
import com.qisstpay.lendingservice.entity.EPCallLog;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface CollectionService {
    QpayCollectionResponseDto collectTroughQpay(QpayCollectionRequestDto collectionRequestDto, LenderCallLog callLog);

    QpayLinkResponseDto getQpayLink(CollectionBillRequestDto billRequestDto, LenderCallLog lenderCallLog);

    QpayCollectionResponseDto qpayCollectionStatus(Long billId, LenderCallLog callLog, String otp);

    EPCollectionInquiryResponse billInquiry(EPCollectionInquiryRequest epCollectionInquiryRequest, EPCallLog savedEpCallLog) throws ParseException;

    EPCollectionBillUpdateResponse billUpdate(EPCollectionBillUpdateRequest epCollectionBillUpdateRequest, EPCallLog savedEpCallLog) throws ParseException;
}
