package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    Optional<Consumer> findByPhoneNumber(String phone);
}
