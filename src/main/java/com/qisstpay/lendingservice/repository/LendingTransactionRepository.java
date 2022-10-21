package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.LendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LendingTransactionRepository extends JpaRepository<LendingTransaction, Long> {
}
