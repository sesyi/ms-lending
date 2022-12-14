package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.hmb.request.GetTransactionStatusRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTokenResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTransactionStatusResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.SubmitTransactionResponseDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.LendingTransaction;

public interface HMBPaymentService {

    TransferResponseDto transfer(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog, Consumer consumer);
    TransactionStateResponse checkTransactionStatus(LendingTransaction lendingTransaction, LenderCallLog lenderCallLog);

    // remove in future
    GetTokenResponseDto callGetTokenApi();
    SubmitTransactionResponseDto callSubmitIFTTransactionApi(String token, SubmitTransactionRequestDto submitTransactionRequestDto);
    SubmitTransactionResponseDto callSubmitIBFTTransactionApi(String token, SubmitTransactionRequestDto submitTransactionRequestDto) throws Exception;
    GetTransactionStatusResponseDto callGetStatusApi(String authToken, GetTransactionStatusRequestDto getTransactionStatusRequestDto) throws Exception;
}
