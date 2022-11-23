package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCreditScoreDataResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerCreditScoreData;
import com.qisstpay.lendingservice.repository.CreditScoreRepository;
import com.qisstpay.lendingservice.service.ConsumerCreditScoreService;
import com.qisstpay.lendingservice.utils.ModelConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConsumerCreditScoreServiceImpl implements ConsumerCreditScoreService {

    @Autowired
    private ModelConverter modelConverter;

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    private final String CALLING_CREDIT_SCORE_SERVICE = "Calling Consumer Credit Score Service";

    @Override
    public ConsumerCreditScoreData create(TasdeeqCreditScoreDataResponseDto creditScoreDataResponseDto, Consumer consumer, String cnic) {
        log.info(CALLING_CREDIT_SCORE_SERVICE);
        ConsumerCreditScoreData consumerCreditScoreData = modelConverter.convertToCreditScoreData(creditScoreDataResponseDto);
        consumerCreditScoreData.setCnic(cnic);
        consumerCreditScoreData.setConsumer(consumer);
        return consumerCreditScoreData;
    }
}
