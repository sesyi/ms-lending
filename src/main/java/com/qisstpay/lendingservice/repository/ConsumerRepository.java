package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    String findByPhoneNumber = "SELECT c FROM Consumer c where c.phoneNumber = ?1 and c.deletedAt is null";
    String findByCnic = "SELECT c FROM Consumer c where c.cnic = ?1 and c.deletedAt is null";
    String findByCnicAndPhoneNumber = "SELECT c FROM Consumer c where c.cnic = ?1 and c.phoneNumber = ?2 and c.deletedAt is null";

    @Query(findByPhoneNumber)
    Optional<Consumer> findByPhoneNumber(String phone);

    @Query(findByCnic)
    Optional<Consumer> findByCnic(String cnic);

    @Query(findByCnicAndPhoneNumber)
    Optional<Consumer> findByCnicAndPhoneNumber(String cnic, String phoneNumber);
}
