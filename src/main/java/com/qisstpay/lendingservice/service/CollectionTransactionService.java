package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.response.CollectionBillResponseDto;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CollectionTransactionService {

    Optional<CollectionTransaction> geById(Long id);

    Optional<CollectionTransaction> geByServiceTransactionId(String serviceTransactionId);

    Optional<CollectionTransaction> geByTransactionId(String transactionId);

    CollectionBillResponseDto geBill(Long id);

    CollectionTransaction save(CollectionTransaction collectionTransaction);
}

