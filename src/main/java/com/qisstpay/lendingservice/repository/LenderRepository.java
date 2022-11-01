package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.Lender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LenderRepository extends JpaRepository<Lender, Long> {
    Boolean existsByUserId(Long userId);

    Optional<Lender> getByUserId(Long userId);
}
