package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.LenderPaymentGateway;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LenderPaymentGatewayRepository extends JpaRepository<LenderPaymentGateway, Long> {

    Optional<LenderPaymentGateway> findByIsDefaultTrue();
}
