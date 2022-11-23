package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCoborrowerDetailResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCreditScoreDataResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerDetailsOfBankruptcyCases;

import java.util.List;

public interface ConsumerCoborrowerDetailService {
    ConsumerDetailsOfBankruptcyCases create(List<TasdeeqCoborrowerDetailResponseDto> coborrowerDetailResponseDtoList, Consumer consumer);
}
