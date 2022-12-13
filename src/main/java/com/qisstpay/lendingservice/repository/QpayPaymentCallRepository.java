package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.QPayPaymentCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QpayPaymentCallRepository extends JpaRepository<QPayPaymentCallLog, Long> {
}
