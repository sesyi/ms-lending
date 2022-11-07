package com.qisstpay.lendingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqReportDataRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqAuthResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface TasdeeqService {
    TasdeeqAuthResponseDto authentication(Long requestId);

    TasdeeqConsumerReportResponseDto getConsumerReport(TasdeeqReportDataRequestDto tasdeeqReportDataRequestDto, String authToken, Long lenderCallId) throws JsonProcessingException;

    Long getLastAuthTokenId();
}

