package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCreditScoreDataResponseDto;
import com.qisstpay.lendingservice.entity.CreditScoreData;
import com.qisstpay.lendingservice.repository.CreditScoreRepository;
import com.qisstpay.lendingservice.service.CreditScoreService;
import com.qisstpay.lendingservice.utils.ModelConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreditScoreServiceImpl implements CreditScoreService {

    @Autowired
    private ModelConverter modelConverter;

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    private final String CALLING_CREDIT_SCORE_SERVICE = "Calling Credit Score Service";

    @Override
    public CreditScoreData save(TasdeeqCreditScoreDataResponseDto creditScoreDataResponseDto, String cnic) {
        log.info(CALLING_CREDIT_SCORE_SERVICE);
        CreditScoreData creditScoreData = modelConverter.convertToCreditScoreData(creditScoreDataResponseDto);
        creditScoreData.setCnic(cnic);
        return creditScoreRepository.save(creditScoreData);
    }
}
