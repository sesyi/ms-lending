package com.qisstpay.lendingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CreditScoreResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface LendingService {
    TransferResponseDto transfer(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog) throws JsonProcessingException;

    TransferResponseDto transferV2(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog, User user) throws JsonProcessingException;

    TransactionStateResponse checkStatus(String transactionId, LenderCallLog lenderCallLog);

    CreditScoreResponseDto checkCreditScore(CreditScoreRequestDto creditScoreRequestDto, LenderCallLog lenderCallLogx) throws JsonProcessingException;
}

