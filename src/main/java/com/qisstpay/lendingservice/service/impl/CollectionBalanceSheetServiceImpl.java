package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.entity.CollectionBalanceSheet;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import com.qisstpay.lendingservice.repository.CollectionBalanceSheetRepository;
import com.qisstpay.lendingservice.service.CollectionBalanceSheetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class CollectionBalanceSheetServiceImpl implements CollectionBalanceSheetService {

    @Autowired
    private CollectionBalanceSheetRepository collectionBalanceSheetRepository;

    @Override
    public void save(CollectionTransaction collectionTransaction) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Double gatewayFee = 0d;
        String gatewayPer = "";
        if (collectionTransaction.getPaymentGateway().equals(PaymentGatewayType.EASYPAISA)) {
            gatewayFee = collectionTransaction.getAmountCollected() * 0.015;
            gatewayPer = "1.5%";
        } else if (collectionTransaction.getPaymentGateway().equals(PaymentGatewayType.NIFT)) {
            gatewayFee = collectionTransaction.getAmountCollected() * 0.02;
            gatewayPer = "2.0%";
        } else if (collectionTransaction.getPaymentGateway().equals(PaymentGatewayType.ALFALAH)) {
            gatewayFee = collectionTransaction.getAmountCollected() * 0.025;
            gatewayPer = "2.5%";
        }
        collectionBalanceSheetRepository.save(
                CollectionBalanceSheet.builder()
                        .debit(collectionTransaction.getAmountCollected())
                        .user(collectionTransaction.getLenderCall().getUser())
                        .collectionTransactionId(collectionTransaction.getServiceTransactionId())
                        .disbursementTransactionStamp(collectionTransaction.getTransactionStamp())
                        .shortDescription(String.format("Debit Against CollectionTransactionId: %s, disbursementTransactionId: %s LenderId: %s, DateTime: %s", collectionTransaction.getServiceTransactionId(), collectionTransaction.getTransactionStamp(), collectionTransaction.getLenderCall().getUser(), dtf.format(LocalDateTime.now())))
                        .build());
        collectionBalanceSheetRepository.save(
                CollectionBalanceSheet.builder()
                        .credit(gatewayFee)
                        .user(collectionTransaction.getLenderCall().getUser())
                        .collectionTransactionId(collectionTransaction.getServiceTransactionId())
                        .disbursementTransactionStamp(collectionTransaction.getTransactionStamp())
                        .shortDescription(String.format("Credit For GatewayType: %s GatewayFee: %s, CollectionTransactionId: %s DateTime: %s", collectionTransaction.getPaymentGateway().getName(), gatewayPer, collectionTransaction.getServiceTransactionId(), dtf.format(LocalDateTime.now())))
                        .build());
        collectionBalanceSheetRepository.save(
                CollectionBalanceSheet.builder()
                        .credit(20d)
                        .user(collectionTransaction.getLenderCall().getUser())
                        .collectionTransactionId(collectionTransaction.getServiceTransactionId())
                        .disbursementTransactionStamp(collectionTransaction.getTransactionStamp())
                        .shortDescription("Credit For Qpay Collection Fee: 20 PKR")
                        .build());
    }
}
