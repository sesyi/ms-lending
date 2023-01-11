package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.entity.LendingTransaction;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LendingTransactionService {

    Optional<LendingTransaction> geById(Long id);

    Optional<LendingTransaction> geByTransactionStamp(String transactionStamp, Long lenderId);
}

