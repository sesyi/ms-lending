package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.Configuration;
import com.qisstpay.lendingservice.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Optional<Configuration> findByServiceType(ServiceType serviceType);

}
