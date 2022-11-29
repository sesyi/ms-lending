package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.communication.PhoneNumberResponseDto;

public interface CommunicationService {

    PhoneNumberResponseDto phoneFormat(final String phoneNumber);
}
