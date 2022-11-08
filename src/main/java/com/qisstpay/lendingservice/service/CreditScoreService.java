package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCreditScoreDataResponseDto;
import com.qisstpay.lendingservice.entity.CreditScoreData;

public interface CreditScoreService {
    CreditScoreData save(TasdeeqCreditScoreDataResponseDto creditScoreDataResponseDto, String cnic);
}
