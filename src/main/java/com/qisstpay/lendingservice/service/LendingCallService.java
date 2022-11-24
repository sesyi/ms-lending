package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.enums.ServiceType;
import org.springframework.stereotype.Service;

@Service
public interface LendingCallService {
    LenderCallLog saveLenderCall(User user, String request, ServiceType serviceType);

    void saveLenderCall(LenderCallLog lenderCallLog);

    LenderCallLog getLendingCall(Long lenderCallId);
}

