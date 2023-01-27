package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.hmb.HMBCredentials;
import com.qisstpay.lendingservice.dto.hmb.request.GetTransactionStatusRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.HMBFetchAccountTitleRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitIBFTTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitIFTTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.response.*;
import com.qisstpay.lendingservice.dto.internal.request.FetchTitleRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.FetchTitleResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.LendingTransaction;

public interface HMBPaymentService {

    FetchTitleResponseDto fetchTitle(FetchTitleRequestDto fetchTitleRequestDto, LenderCallLog lenderCallLog);
    TransferResponseDto transfer(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog, Consumer consumer);
    TransactionStateResponse checkTransactionStatus(LendingTransaction lendingTransaction, LenderCallLog lenderCallLog);
    TransactionStateResponse checkTransactionStatusInternal(LendingTransaction lendingTransaction);

    // remove in future
    GetTokenResponseDto callGetTokenApi(HMBCredentials hmbCredentials);
    HMBFetchAccountTitleResponseDto callFetchTitleApi(HMBCredentials hmbCredentials, String authToken, HMBFetchAccountTitleRequestDto getTransactionStatusRequestDto) throws Exception;
    SubmitIFTTransactionResponseDto callSubmitIFTTransactionApi(HMBCredentials hmbCredentials, String token, SubmitIFTTransactionRequestDto submitIFTTransactionRequestDto);
    SubmitIBFTTransactionResponseDto callSubmitIBFTTransactionApi(HMBCredentials hmbCredentials, String token, SubmitIBFTTransactionRequestDto IBFTSubmitTransactionRequestDto) throws Exception;
    GetTransactionStatusResponseDto callGetStatusApi(HMBCredentials hmbCredentials, String authToken, GetTransactionStatusRequestDto getTransactionStatusRequestDto) throws Exception;
}
