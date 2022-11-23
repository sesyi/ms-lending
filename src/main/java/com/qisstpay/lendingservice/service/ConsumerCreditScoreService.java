package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCreditScoreDataResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerCreditScoreData;

public interface ConsumerCreditScoreService {
    ConsumerCreditScoreData create(TasdeeqCreditScoreDataResponseDto creditScoreDataResponseDto, Consumer consumer, String cnic);
}
