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
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CollectionBalanceSheetServiceImpl implements CollectionBalanceSheetService {

    @Autowired
    private CollectionBalanceSheetRepository collectionBalanceSheetRepository;

    @Override
    public void createBalanceEntryAndSave(CollectionTransaction collectionTransaction) {
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
        List<CollectionBalanceSheet> collectionBalanceSheets = new ArrayList<>();
        collectionBalanceSheets.add(
                CollectionBalanceSheet.builder()
                        .credit(collectionTransaction.getAmountCollected())
                        .user(collectionTransaction.getLenderCall().getUser())
                        .collectionTransactionId(collectionTransaction.getServiceTransactionId())
                        .disbursementTransactionStamp(collectionTransaction.getTransactionStamp())
                        .shortDescription(String.format("CollectionTransactionId: %s, disbursementTransactionId: %s, LenderId: %s, DateTime: %s", collectionTransaction.getServiceTransactionId(), collectionTransaction.getTransactionStamp(), collectionTransaction.getLenderCall().getUser().getId(), dtf.format(LocalDateTime.now())))
                        .build());
        collectionBalanceSheets.add(
                CollectionBalanceSheet.builder()
                        .debit(gatewayFee)
                        .user(collectionTransaction.getLenderCall().getUser())
                        .collectionTransactionId(collectionTransaction.getServiceTransactionId())
                        .disbursementTransactionStamp(collectionTransaction.getTransactionStamp())
                        .shortDescription(String.format("GatewayFee: %s, GatewayType: %s, CollectionTransactionId: %s, DateTime: %s", gatewayPer, collectionTransaction.getPaymentGateway().getName(), collectionTransaction.getServiceTransactionId(), dtf.format(LocalDateTime.now())))
                        .build());
        collectionBalanceSheets.add(
                CollectionBalanceSheet.builder()
                        .debit(20d)
                        .user(collectionTransaction.getLenderCall().getUser())
                        .collectionTransactionId(collectionTransaction.getServiceTransactionId())
                        .disbursementTransactionStamp(collectionTransaction.getTransactionStamp())
                        .shortDescription("Qpay Collection Fee: 20 PKR")
                        .build());
        save(collectionBalanceSheets);
    }

    @Override
    public void save(List<CollectionBalanceSheet> collectionBalanceSheets) {
        collectionBalanceSheetRepository.saveAll(collectionBalanceSheets);
    }
}
