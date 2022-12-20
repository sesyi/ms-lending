package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.qpay.request.QpayCaptureRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.QpayPaymentRequestDto;
import com.qisstpay.lendingservice.dto.qpay.response.QpayPaymentResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import org.springframework.stereotype.Service;

@Service
public interface QpayPaymentService {
    QpayPaymentResponseDto payment(QpayPaymentRequestDto paymentRequestDto, LenderCallLog callLog);

    QpayPaymentResponseDto status(String transactionIdAndGateway, LenderCallLog callLog);

    QpayPaymentResponseDto capture(QpayCaptureRequestDto captureRequestDto, LenderCallLog callLog, String otp);
}

