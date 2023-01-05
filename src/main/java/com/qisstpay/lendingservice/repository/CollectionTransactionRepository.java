package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.CollectionTransaction;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.enums.TransactionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionTransactionRepository extends JpaRepository<CollectionTransaction, Long> {
    Optional<CollectionTransaction> findTopByConsumerAndTransactionStateOrderByCreatedAtDesc(Consumer consumer, TransactionState transactionState);

    Optional<CollectionTransaction> findByServiceTransactionId(String serviceTransactionId);

    Optional<CollectionTransaction> findByTransactionStamp(String transactionStamp);
}
