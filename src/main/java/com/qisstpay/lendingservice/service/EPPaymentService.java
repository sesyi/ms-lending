package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.easypaisa.request.EPInquiryRequestDto;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPInquiryResponseDto;
import com.qisstpay.lendingservice.dto.internal.request.InquiryRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.InquiryResponseDto;

public interface EPPaymentService {
    public EPInquiryResponseDto epMAToMaInquiry(EPInquiryRequestDto epInquiryRequestDto);
}
