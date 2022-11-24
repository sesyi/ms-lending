package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUserId(Long userId);

    Optional<User> getByUserId(Long userId);

    Optional<User> getByUserName(String userName);
}
