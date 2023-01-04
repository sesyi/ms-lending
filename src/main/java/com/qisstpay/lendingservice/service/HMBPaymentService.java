package com.qisstpay.lendingservice.service;

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

    // remove in future
    GetTokenResponseDto callGetTokenApi();
    HMBFetchAccountTitleResponseDto callFetchTitleApi(String authToken, HMBFetchAccountTitleRequestDto getTransactionStatusRequestDto) throws Exception;
    SubmitIFTTransactionResponseDto callSubmitIFTTransactionApi(String token, SubmitIFTTransactionRequestDto submitIFTTransactionRequestDto);
    SubmitIBFTTransactionResponseDto callSubmitIBFTTransactionApi(String token, SubmitIBFTTransactionRequestDto IBFTSubmitTransactionRequestDto) throws Exception;
    GetTransactionStatusResponseDto callGetStatusApi(String authToken, GetTransactionStatusRequestDto getTransactionStatusRequestDto) throws Exception;
}
