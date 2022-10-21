package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface LendingService {
    TransferResponseDto transfer(TransferRequestDto transferRequestDto);
}

