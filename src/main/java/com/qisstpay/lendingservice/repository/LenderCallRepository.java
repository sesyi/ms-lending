package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.LenderCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LenderCallRepository extends JpaRepository<LenderCallLog, Long> {
}
