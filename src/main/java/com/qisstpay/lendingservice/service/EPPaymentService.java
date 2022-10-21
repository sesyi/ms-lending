package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.easypaisa.request.EPRequestDto;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPInquiryResponseDto;

public interface EPPaymentService {
    public EPInquiryResponseDto epMAToMaInquiry(EPRequestDto epRequestDto);
}
