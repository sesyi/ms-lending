package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.entity.Lender;
import com.qisstpay.lendingservice.entity.LenderCallsHistory;
import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.repository.LenderCallRepository;
import com.qisstpay.lendingservice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LendingCallServiceImpl implements LendingCallService {


    @Autowired
    private LenderCallRepository lenderCallRepository;

    @Override
    public LenderCallsHistory saveLenderCall(Lender lender, String request, ServiceType serviceType){
        return  lenderCallRepository.save(LenderCallsHistory.builder().lender(lender).request(request).serviceType(serviceType).build());
    }

    @Override
    public void saveLenderCall(LenderCallsHistory lenderCallsHistory) {
        lenderCallRepository.save(lenderCallsHistory);
    }

    @Override
    public LenderCallsHistory getLendingCall(Long lenderCallId) {
        return lenderCallRepository.getById(lenderCallId);
    }
}
