package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.Bank;
import com.qisstpay.lendingservice.entity.HMBBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HMBBankRepository extends JpaRepository<HMBBank, Long> {

    HMBBank findByBankId(Long bankId);
}
