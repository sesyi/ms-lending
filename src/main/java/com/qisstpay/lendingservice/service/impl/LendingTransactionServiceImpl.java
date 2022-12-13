package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.entity.LendingTransaction;
import com.qisstpay.lendingservice.error.errortype.LendingTransactionErrorType;
import com.qisstpay.lendingservice.repository.LendingTransactionRepository;
import com.qisstpay.lendingservice.service.LendingTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class LendingTransactionServiceImpl implements LendingTransactionService {

    @Autowired
    private LendingTransactionRepository lendingTransactionRepository;

    @Override
    public Optional<LendingTransaction> geById(Long id) {
        Optional<LendingTransaction> lendingTransaction = lendingTransactionRepository.findById(id);
        if (lendingTransaction.isPresent()) {
            return lendingTransaction;
        } else {
            log.error(LendingTransactionErrorType.ENABLE_TO_GET_TRANSACTION.getErrorMessage());
            throw new ServiceException(LendingTransactionErrorType.ENABLE_TO_GET_TRANSACTION);
        }
    }

    @Override
    public Optional<LendingTransaction> geByIdentityNumber(String identityNumber) {
        Optional<LendingTransaction> lendingTransaction = lendingTransactionRepository.findByIdentityNumber(identityNumber);
        if (lendingTransaction.isPresent()) {
            return lendingTransaction;
        } else {
            log.error(LendingTransactionErrorType.INVALID_IDENTITY_NUMBER.getErrorMessage());
            throw new ServiceException(LendingTransactionErrorType.INVALID_IDENTITY_NUMBER);
        }
    }
}
