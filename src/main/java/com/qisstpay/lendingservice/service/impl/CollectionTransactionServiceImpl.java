package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.internal.response.CollectionBillResponseDto;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import com.qisstpay.lendingservice.error.errortype.BillErrorType;
import com.qisstpay.lendingservice.error.errortype.LendingTransactionErrorType;
import com.qisstpay.lendingservice.repository.CollectionTransactionRepository;
import com.qisstpay.lendingservice.service.CollectionTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Optional;

@Component
@Slf4j
public class CollectionTransactionServiceImpl implements CollectionTransactionService {

    @Autowired
    private CollectionTransactionRepository collectionTransactionRepository;

    @Override
    public Optional<CollectionTransaction> geById(Long id) {
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findById(id);
        if (collectionTransaction.isPresent()) {
            return collectionTransaction;
        } else {
            log.error(BillErrorType.ENABLE_TO_GET_BILL.getErrorMessage());
            throw new ServiceException(BillErrorType.ENABLE_TO_GET_BILL);
        }
    }

    @Override
    public CollectionBillResponseDto geBill(Long id) {
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findById(id);
        if (collectionTransaction.isPresent()) {
            return CollectionBillResponseDto.builder()
                    .billId(collectionTransaction.get().getId())
                    .amount(collectionTransaction.get().getAmount())
                    .amountAfterDueDate(collectionTransaction.get().getAmountAfterDueDate())
                    .dueDate(new Timestamp(collectionTransaction.get().getDueDate().getTime()))
                    .identityNumber(collectionTransaction.get().getIdentityNumber())
                    .userName(collectionTransaction.get().getUserName())
                    .billStatus(collectionTransaction.get().getBillStatus())
                    .consumerId(collectionTransaction.get().getConsumer().getId().toString())
                    .consumerEmail(collectionTransaction.get().getConsumer().getEmail())
                    .build();
        } else {
            log.error(BillErrorType.ENABLE_TO_GET_BILL.getErrorMessage());
            throw new ServiceException(BillErrorType.ENABLE_TO_GET_BILL);
        }
    }

    @Override
    public Optional<CollectionTransaction> geByIdentityNumber(String identityNumber) {
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findByIdentityNumber(identityNumber);
        if (collectionTransaction.isPresent()) {
            return collectionTransaction;
        } else {
            log.error(LendingTransactionErrorType.INVALID_IDENTITY_NUMBER.getErrorMessage());
            throw new ServiceException(BillErrorType.INVALID_IDENTITY_NUMBER);
        }
    }

    @Override
    public CollectionTransaction save(CollectionTransaction collectionTransaction) {
        return collectionTransactionRepository.save(collectionTransaction);
    }
}
