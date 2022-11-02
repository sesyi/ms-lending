package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.request.LenderUserRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.entity.Lender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LenderService {
    MessageResponseDto saveLender(LenderUserRequestDto lenderUserRequestDto);

    MessageResponseDto verifyUser(Long userId, String apiKey);

    Optional<Lender> getLender(Long userId);
}

