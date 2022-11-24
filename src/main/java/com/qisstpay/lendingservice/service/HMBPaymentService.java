package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.hmb.request.SubmitIFTTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTokenResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.SubmitIBFTTransactionResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.SubmitIFTTransactionResponseDto;

public interface HMBPaymentService {

    GetTokenResponseDto getToken();
    SubmitIFTTransactionResponseDto submitIFTTransaction(String token, SubmitIFTTransactionRequestDto submitIFTTransactionRequestDto);
    SubmitIBFTTransactionResponseDto submitIBFTTransaction(String token, SubmitIBFTTransactionResponseDto submitIBFTTransactionResponseDto);

}
