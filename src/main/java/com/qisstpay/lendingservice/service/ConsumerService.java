package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import org.springframework.stereotype.Service;

@Service
public interface ConsumerService {
    Consumer getOrCreateConsumerDetails(TasdeeqConsumerReportResponseDto tasdeeqConsumerReportResponseDto, String phoneNumber);

    Consumer save(Consumer consumer);
}
