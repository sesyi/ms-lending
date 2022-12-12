package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.entity.Bank;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerAccount;
import com.qisstpay.lendingservice.repository.ConsumerAccountRepository;
import com.qisstpay.lendingservice.service.ConsumerAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ConsumerAccountServiceImpl implements ConsumerAccountService {

    @Autowired
    private ConsumerAccountRepository consumerAccountRepository;

    @Override
    public Optional<ConsumerAccount> geByAccountNumOrIBAN(String accountNumber) {
        return consumerAccountRepository.findByAccountNumberOrIBAN(accountNumber);
    }

    @Override
    public ConsumerAccount createAccount(String accountNumberOrIBAN, Bank bank, Consumer consumer) {
        String accountNum = null;
        String ibanNum = null;
        if (accountNumberOrIBAN.matches("[0-9]+")) {
            accountNum = accountNumberOrIBAN;
        } else {
            ibanNum = accountNumberOrIBAN;
        }
        return consumerAccountRepository.save(ConsumerAccount.builder()
                .verifiedCheck(Boolean.FALSE)
                .ibanNumber(ibanNum)
                .accountNumber(accountNum)
                .bank(bank)
                .consumer(consumer)
                .build());
    }

    @Override
    public ConsumerAccount updateAccount(ConsumerAccount consumerAccount) {
        return consumerAccountRepository.save(consumerAccount);
    }
}
