package com.qisstpay.lendingservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqReportDataRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqAuthResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerPersonalInformationResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCreditScoreDataResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerCreditScoreData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ModelConverter {
    private final ModelMapper  modelMapper;
    private final ObjectMapper objectMapper;


    public Consumer convertToConsumer(TasdeeqConsumerPersonalInformationResponseDto consumerInfo) {
        Consumer consumer = modelMapper.map(consumerInfo, Consumer.class);
        consumer.setConsumerCreditScoreData(new ArrayList<>());
        consumer.setSummaryOverdue24Ms(new ArrayList<>());
        consumer.setDetailsOfStatusCreditApplications(new ArrayList<>());
        consumer.setDetailsOfLoansSettlements(new ArrayList<>());
        consumer.setPersonalGuarantees(new ArrayList<>());
        consumer.setCoborrowerDetails(new ArrayList<>());
        consumer.setDetailsOfBankruptcyCases(new ArrayList<>());
        consumer.setCreditEnquiries(new ArrayList<>());
        consumer.setLoanDetails(new ArrayList<>());
        consumer.setCreditHistories(new ArrayList<>());
        return consumer;
    }

    public TasdeeqAuthResponseDto convertTOTasdeeqAuthResponseDto(Object object) {
        return modelMapper.map(object, TasdeeqAuthResponseDto.class);
    }


    public TasdeeqConsumerReportResponseDto convertTOTasdeeqConsumerReportResponseDto(Object object) throws JsonProcessingException {
        return modelMapper.map(object, TasdeeqConsumerReportResponseDto.class);
    }

    public ConsumerCreditScoreData convertToCreditScoreData(TasdeeqCreditScoreDataResponseDto creditScoreDataResponseDto) {
        return modelMapper.map(creditScoreDataResponseDto, ConsumerCreditScoreData.class);
    }

    public TasdeeqReportDataRequestDto convertToTasdeeqReportDataRequestDto(CreditScoreRequestDto creditScoreRequestDto) {
        return modelMapper.map(creditScoreRequestDto, TasdeeqReportDataRequestDto.class);
    }
}
