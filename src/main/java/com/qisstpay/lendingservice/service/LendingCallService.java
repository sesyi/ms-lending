package com.qisstpay.lendingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CreditScoreResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.Lender;
import com.qisstpay.lendingservice.entity.LenderCallsHistory;
import com.qisstpay.lendingservice.enums.ServiceType;
import org.springframework.stereotype.Service;

@Service
public interface LendingCallService {
    LenderCallsHistory saveLenderCall(Lender lender, String request, ServiceType serviceType);

    void saveLenderCall(LenderCallsHistory lenderCallsHistory);

    LenderCallsHistory getLendingCall(Long lenderCallId);
}

