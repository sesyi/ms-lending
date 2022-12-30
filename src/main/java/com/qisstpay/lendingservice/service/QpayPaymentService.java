package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.qpay.request.QpayCaptureRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.QpayPaymentRequestDto;
import com.qisstpay.lendingservice.dto.qpay.response.QpayPaymentResponseDto;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import com.qisstpay.lendingservice.entity.ConsumerAccount;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.QpayPaymentTransaction;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface QpayPaymentService {
    QpayPaymentTransaction payment(QpayPaymentRequestDto paymentRequestDto, LenderCallLog callLog, CollectionTransaction collectionTransaction, Optional<ConsumerAccount> account);

    QpayPaymentResponseDto status(String transactionIdAndGateway, LenderCallLog callLog);

    QpayPaymentResponseDto capture(QpayCaptureRequestDto captureRequestDto, LenderCallLog callLog);
}

