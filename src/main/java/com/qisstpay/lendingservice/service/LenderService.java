package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.request.LenderUserRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface LenderService {
    MessageResponseDto saveLender(LenderUserRequestDto lenderUserRequestDto);

    MessageResponseDto verifyUser(Long userId, String apiKey);
}

