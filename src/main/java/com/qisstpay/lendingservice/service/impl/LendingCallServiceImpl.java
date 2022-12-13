package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.CallType;
import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.repository.LenderCallRepository;
import com.qisstpay.lendingservice.service.LendingCallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LendingCallServiceImpl implements LendingCallService {


    @Autowired
    private LenderCallRepository lenderCallRepository;

    @Override
    public LenderCallLog saveLenderCall(User user, String request, ServiceType serviceType, CallType callType) {
        return lenderCallRepository.save(LenderCallLog.builder().user(user).request(request).serviceType(serviceType).callType(callType).build());
    }

    @Override
    public LenderCallLog saveLenderCall(String request, ServiceType serviceType, CallType callType) {
        return lenderCallRepository.save(LenderCallLog.builder().request(request).serviceType(serviceType).callType(callType).build());
    }

    @Override
    public void saveLenderCall(LenderCallLog lenderCallLog) {
        lenderCallRepository.save(lenderCallLog);
    }

    @Override
    public LenderCallLog getLendingCall(Long lenderCallId) {
        return lenderCallRepository.getById(lenderCallId);
    }
}
