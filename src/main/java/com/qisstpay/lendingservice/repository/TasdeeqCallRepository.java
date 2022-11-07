package com.qisstpay.lendingservice.repository;

import com.qisstpay.lendingservice.entity.TasdeeqCallsHistory;
import com.qisstpay.lendingservice.enums.EndPointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TasdeeqCallRepository extends JpaRepository<TasdeeqCallsHistory, Long> {
    String getLastTokenId = "SELECT id FROM tasdeeq_calls_history where end_point = 'AUTH' and status = 'SUCCESS' ORDER BY id DESC LIMIT 1";

    @Query(value = getLastTokenId, nativeQuery = true)
    Long findLastTokenId();

}
