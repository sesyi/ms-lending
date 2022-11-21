package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.request.ConfigRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;

public interface ConfigurationService {
    MessageResponseDto updateConfiguration(ConfigRequestDto configRequestDto);
}
