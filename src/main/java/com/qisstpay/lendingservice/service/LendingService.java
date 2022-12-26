package com.qisstpay.lendingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.lendingservice.controller.CollectionController;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CreditScoreResponseDto;
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
import org.springframework.stereotype.Service;

@Service
public interface LendingService {
    TransferResponseDto transfer(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog) throws JsonProcessingException;

    TransactionStateResponse checkStatus(String transactionId, LenderCallLog lenderCallLog);

    CreditScoreResponseDto checkCreditScore(CreditScoreRequestDto creditScoreRequestDto, LenderCallLog lenderCallLogx) throws JsonProcessingException;

    EPCallLog addEPCalLog(EndPointType type, String request, LendingTransaction lendingTransaction, CallType callType, User user);

    EPCallLog updateEpCallLog(EPCallLog savedEpCallLogs, CallStatusType status, String responseCode, String message, String response, CollectionTransaction collectionTransaction);

    void updateLenderCallLog(CallStatusType status, String description, LenderCallLog lenderCallLog);
}

