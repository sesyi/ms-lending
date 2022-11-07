package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.CreditScoreData;
import com.qisstpay.lendingservice.entity.TasdeeqCallsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditScoreRepository extends JpaRepository<CreditScoreData, Long> {

}
