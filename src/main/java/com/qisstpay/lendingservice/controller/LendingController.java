package com.qisstpay.lendingservice.controller;

import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.request.InquiryRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.InquiryResponseDto;
import com.qisstpay.lendingservice.service.LendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/lending/v1")
@RequiredArgsConstructor
public class LendingController {


    private static final String INQUIRY = "/matoma/inquiry";

    private final LendingService lendingService;

    @GetMapping(INQUIRY)
    public CustomResponse<InquiryResponseDto> maToMaInquiry(@RequestBody InquiryRequestDto inquiryRequestDto) {
        return CustomResponse.CustomResponseBuilder.<InquiryResponseDto>builder()
                .body(lendingService.maToMaInquiry(inquiryRequestDto)).build();
    }
}
