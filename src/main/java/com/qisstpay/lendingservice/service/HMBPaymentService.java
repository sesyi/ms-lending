package com.qisstpay.lendingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.lendingservice.dto.hmb.request.GetTransactionStatusRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTokenResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTransactionStatusResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.SubmitTransactionResponseDto;

public interface HMBPaymentService {

    GetTokenResponseDto getToken();
    SubmitTransactionResponseDto submitIFTTransaction(String token, SubmitTransactionRequestDto submitTransactionRequestDto);
    SubmitTransactionResponseDto submitIBFTTransaction(String token, SubmitTransactionRequestDto submitTransactionRequestDto) throws Exception;
    GetTransactionStatusResponseDto getStatus(String authToken, GetTransactionStatusRequestDto getTransactionStatusRequestDto) throws Exception;
}
