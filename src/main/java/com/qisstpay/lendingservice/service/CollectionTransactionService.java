package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.response.CollectionBillResponseDto;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CollectionTransactionService {

    Optional<CollectionTransaction> geById(Long id);

    CollectionBillResponseDto geBill(Long id);

    Optional<CollectionTransaction> geByIdentityNumber(String identityNumber);

    CollectionTransaction save(CollectionTransaction collectionTransaction);
}

