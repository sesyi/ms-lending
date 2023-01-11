package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.request.ConfigRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.entity.Configuration;
import com.qisstpay.lendingservice.enums.ServiceType;

public interface ConfigurationService {
    MessageResponseDto updateConfiguration(ConfigRequestDto configRequestDto);

    Configuration getConfigurationByLenderIdAndServiceType(Long lenderId, ServiceType serviceType);
}
