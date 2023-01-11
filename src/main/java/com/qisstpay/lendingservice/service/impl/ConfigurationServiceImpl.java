package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.internal.request.ConfigRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.entity.Configuration;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.repository.ConfigurationRepository;
import com.qisstpay.lendingservice.service.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RefreshScope
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    ConfigurationRepository configurationRepository;

    private static final String SERVICE_CALLED = "ConfigurationService Called";
    private static final String MESSAGE        = "Service Type: {} Description: {} Updated";

    @Override
    public MessageResponseDto updateConfiguration(ConfigRequestDto configRequestDto) {
        log.info(SERVICE_CALLED);
        log.info("updateConfiguration ConfigRequestDto: {}", configRequestDto);
        Optional<Configuration> configuration = configurationRepository.findByServiceType(configRequestDto.getServiceType());
        if (configuration.isPresent()) {
            configuration.get().setActiveStatus(Boolean.FALSE);
            configurationRepository.save(configuration.get());
        }
        configurationRepository.save(
                Configuration.builder()
                        .activeStatus(Boolean.TRUE)
                        .charge(configRequestDto.getCharge())
                        .serviceType(configRequestDto.getServiceType())
                        .description(configRequestDto.getDescription()).build());
        return MessageResponseDto.builder()
                .success(Boolean.TRUE)
                .message(String.format(MESSAGE, configRequestDto.getServiceType(), configRequestDto.getDescription()))
                .build();
    }

    @Override
    public Configuration getConfigurationByLenderIdAndServiceType(Long lenderId, ServiceType serviceType) {
        User user = new User();
        user.setId(lenderId);
        return configurationRepository.findByLenderUserAndServiceTypeAndDefaultValueIsTrue(user, serviceType).orElse(null);
    }

}
