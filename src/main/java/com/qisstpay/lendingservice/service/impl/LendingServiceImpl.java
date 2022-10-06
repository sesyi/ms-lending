package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.internal.request.InquiryRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.InquiryResponseDto;
import com.qisstpay.lendingservice.service.LendingService;

public class LendingServiceImpl implements LendingService {
    @Override
    public InquiryResponseDto maToMaInquiry(InquiryRequestDto inquiryRequestDto) {
        return InquiryResponseDto.builder().build();
    }
}
