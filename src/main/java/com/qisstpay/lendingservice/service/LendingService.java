package com.qisstpay.lendingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.lendingservice.controller.CollectionController;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.FetchTitleRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CreditScoreResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.FetchTitleResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import com.qisstpay.lendingservice.entity.EPCallLog;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.LendingTransaction;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.CallType;
import com.qisstpay.lendingservice.enums.EndPointType;
import com.qisstpay.lendingservice.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface LendingService {
    FetchTitleResponseDto fetchTitle(FetchTitleRequestDto fetchTitleRequestDto, LenderCallLog lenderCallLog) throws JsonProcessingException;

    TransferResponseDto transfer(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog) throws JsonProcessingException;

    TransferResponseDto transferV2(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog, User user) throws JsonProcessingException;

    TransactionStateResponse checkStatus(String transactionId, LenderCallLog lenderCallLog);
    TransactionStateResponse checkStatusInternal(String transactionId);

    CreditScoreResponseDto checkCreditScore(CreditScoreRequestDto creditScoreRequestDto, LenderCallLog lenderCallLogx) throws JsonProcessingException;

    EPCallLog addEPCalLog(EndPointType type, String request, LendingTransaction lendingTransaction, CallType callType, User user);

    EPCallLog updateEpCallLog(EPCallLog savedEpCallLogs, CallStatusType status, String responseCode, String message, String response, CollectionTransaction collectionTransaction);

    void updateLenderCallLog(CallStatusType status, String description, LenderCallLog lenderCallLog);
}

