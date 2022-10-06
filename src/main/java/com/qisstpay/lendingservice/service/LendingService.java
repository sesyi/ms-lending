package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.request.InquiryRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.InquiryResponseDto;

public interface LendingService {
    InquiryResponseDto maToMaInquiry(InquiryRequestDto inquiryRequestDto);
}

