package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.ConsumerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerAccountRepository extends JpaRepository<ConsumerAccount, Long> {
    String findByAccountNumberOrIBAN = "SELECT a FROM ConsumerAccount a where a.accountNumber = ?1 OR a.ibanNumber = ?1";

    @Query(findByAccountNumberOrIBAN)
    Optional<ConsumerAccount> findByAccountNumberOrIBAN(String accountNumber);
}
