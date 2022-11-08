package com.qisstpay.lendingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CreditScoreResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface LendingService {
    TransferResponseDto transfer(TransferRequestDto transferRequestDto) throws JsonProcessingException;

    TransactionStateResponse checkStatus(String transactionId);

    CreditScoreResponseDto checkCreditScore(CreditScoreRequestDto creditScoreRequestDto, Long lenderCallId) throws JsonProcessingException;
}

