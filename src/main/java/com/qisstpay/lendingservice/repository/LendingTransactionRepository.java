package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.LendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LendingTransactionRepository extends JpaRepository<LendingTransaction, Long> {

    LendingTransaction findFirstByOrderByIdDesc();

    Optional<LendingTransaction> findByIdentityNumber(String identityNumber);

    Optional<LendingTransaction> findByTransactionStamp(String transactionStamp);
}
