package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.entity.Bank;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerAccount;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ConsumerAccountService {

    Optional<ConsumerAccount> geByAccountNumOrIBAN(String accountNumber);

    ConsumerAccount createAccount(String accountNumberOrIBAN, Bank bank, Consumer consumer);

    ConsumerAccount updateAccount(ConsumerAccount consumerAccount);
}

