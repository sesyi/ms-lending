package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCoborrowerDetailResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCreditScoreDataResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerCreditScoreData;
import com.qisstpay.lendingservice.entity.ConsumerDetailsOfBankruptcyCases;
import com.qisstpay.lendingservice.repository.CreditScoreRepository;
import com.qisstpay.lendingservice.service.ConsumerCoborrowerDetailService;
import com.qisstpay.lendingservice.utils.ModelConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ConsumerCoborrowerDetailServiceImpl implements ConsumerCoborrowerDetailService {

    @Autowired
    private ModelConverter modelConverter;

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    private final String CALLING_SERVICE = "Calling Consumer Coborrower Detail Service";

    @Override
    public ConsumerDetailsOfBankruptcyCases create(List<TasdeeqCoborrowerDetailResponseDto> coborrowerDetailResponseDtoList, Consumer consumer) {
        log.info(CALLING_SERVICE);
//        ConsumerDetailsOfBankruptcyCases creditScoreDataResponseDto = modelConverter.convertToCreditScoreData(coborrowerDetailResponseDtoList);
        return null;
    }
}
