package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.CollectionTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionTransactionRepository extends JpaRepository<CollectionTransaction, Long> {

    Optional<CollectionTransaction> findByIdentityNumber(String identityNumber);
}
