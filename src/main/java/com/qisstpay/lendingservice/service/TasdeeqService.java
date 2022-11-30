package com.qisstpay.lendingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqReportDataRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqAuthResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import org.springframework.stereotype.Service;

@Service
public interface TasdeeqService {
    TasdeeqAuthResponseDto authentication(Long requestId, Boolean clearCache);

    TasdeeqConsumerReportResponseDto getConsumerReport(TasdeeqReportDataRequestDto tasdeeqReportDataRequestDto, LenderCallLog lenderCallLog, TasdeeqAuthResponseDto authentication, Long authTokenId) throws JsonProcessingException;

    Long getLastAuthTokenId();
}

