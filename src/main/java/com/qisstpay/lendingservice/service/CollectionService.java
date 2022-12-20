package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.request.CollectionBillRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.QpayCollectionRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayCollectionResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayLinkResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionBillUpdateRequest;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionBillUpdateResponse;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionInquiryResponse;
import org.springframework.stereotype.Service;

@Service
public interface CollectionService {
    QpayCollectionResponseDto collectTroughQpay(QpayCollectionRequestDto collectionRequestDto, LenderCallLog callLog);

    QpayLinkResponseDto getQpayLink(CollectionBillRequestDto billRequestDto, LenderCallLog lenderCallLog);

    QpayCollectionResponseDto qpayCollectionStatus(Long billId, PaymentGatewayType gatewayType, LenderCallLog callLog, String otp);

    EPCollectionInquiryResponse billInquiry(EPCollectionInquiryRequest epCollectionInquiryRequest);

    EPCollectionBillUpdateResponse billUpdate(EPCollectionBillUpdateRequest epCollectionBillUpdateRequest);
}
