package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.Bank;
import com.qisstpay.lendingservice.entity.CollectionBalanceSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionBalanceSheetRepository extends JpaRepository<CollectionBalanceSheet, Long> {
}
