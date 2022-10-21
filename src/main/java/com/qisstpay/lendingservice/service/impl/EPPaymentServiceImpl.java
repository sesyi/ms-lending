package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.easypaisa.request.EPRequestDto;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPInquiryResponseDto;
import com.qisstpay.lendingservice.service.EPPaymentService;

public class EPPaymentServiceImpl implements EPPaymentService {
    @Override
    public EPInquiryResponseDto epMAToMaInquiry(EPRequestDto epRequestDto) {
        return EPInquiryResponseDto.builder().build();
    }
}
