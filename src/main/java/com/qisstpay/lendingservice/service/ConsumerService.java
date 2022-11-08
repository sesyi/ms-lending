package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerPersonalInformationResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import org.springframework.stereotype.Service;

@Service
public interface ConsumerService {
    Consumer getOrCreateConsumer(TasdeeqConsumerPersonalInformationResponseDto consumerInfo, String phoneNumber);

    Consumer save(Consumer consumer);
}
