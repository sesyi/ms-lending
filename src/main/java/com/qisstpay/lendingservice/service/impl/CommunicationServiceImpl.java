package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.enums.SlackTagType;
import com.qisstpay.commons.error.errortype.CommunicationErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.communication.PhoneNumberResponseDto;
import com.qisstpay.lendingservice.service.CommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RefreshScope
@Slf4j
public class CommunicationServiceImpl implements CommunicationService {

    @Value("${gateway.communication-phone-formatter-service}")
    private String communicationPhoneFormatterServiceUrl;

    @Value("${environment}")
    private String environment;

    @Value("${message.slack.channel.third-party-errors}")
    private String slackChannel;

    private final RestTemplate restTemplate;

    @Autowired
    public CommunicationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PhoneNumberResponseDto phoneFormat(final String phoneNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(phoneNumber, headers);
        try {
            return restTemplate.postForObject(communicationPhoneFormatterServiceUrl, request, PhoneNumberResponseDto.class);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ServiceException(CommunicationErrorType.PHONE_FORMAT_FAILED,
                    ex,
                    HttpMethod.POST.toString(),
                    communicationPhoneFormatterServiceUrl, request, environment,
                    SlackTagType.JAVA,
                    slackChannel
            );
        }
    }
}
