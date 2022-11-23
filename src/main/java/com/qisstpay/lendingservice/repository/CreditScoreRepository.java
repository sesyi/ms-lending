package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.ConsumerCreditScoreData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditScoreRepository extends JpaRepository<ConsumerCreditScoreData, Long> {

}
