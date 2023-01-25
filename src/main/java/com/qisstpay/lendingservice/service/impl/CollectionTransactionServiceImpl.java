package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.internal.response.CollectionBillResponseDto;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import com.qisstpay.lendingservice.entity.QpayPaymentTransaction;
import com.qisstpay.lendingservice.error.errortype.BillErrorType;
import com.qisstpay.lendingservice.repository.CollectionTransactionRepository;
import com.qisstpay.lendingservice.service.CollectionTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Optional;

@Component
@Slf4j
public class CollectionTransactionServiceImpl implements CollectionTransactionService {

    @Autowired
    private CollectionTransactionRepository collectionTransactionRepository;

    @Override
    public Optional<CollectionTransaction> geById(Long id) {
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findById(id);
        if (collectionTransaction.isPresent()) {
            return collectionTransaction;
        } else {
            log.error(BillErrorType.ENABLE_TO_GET_BILL.getErrorMessage());
            throw new ServiceException(BillErrorType.ENABLE_TO_GET_BILL);
        }
    }

    @Override
    public Optional<CollectionTransaction> geByServiceTransactionId(String serviceTransactionId) {
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findByServiceTransactionId(serviceTransactionId);
        if (collectionTransaction.isPresent()) {
            return collectionTransaction;
        } else {
            log.error(BillErrorType.ENABLE_TO_GET_BILL.getErrorMessage());
            throw new ServiceException(BillErrorType.ENABLE_TO_GET_BILL);
        }
    }

    @Override
    public Optional<CollectionTransaction> geByTransactionId(String transactionStamp) {
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findByTransactionStamp(transactionStamp);
        if (collectionTransaction.isPresent()) {
            return collectionTransaction;
        } else {
            log.error(BillErrorType.ENABLE_TO_GET_BILL.getErrorMessage());
            throw new ServiceException(BillErrorType.ENABLE_TO_GET_BILL);
        }
    }

    @Override
    public CollectionBillResponseDto geBill(Long id) {
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findById(id);
        if (collectionTransaction.isPresent()) {
            QpayPaymentTransaction qpayPaymentTransaction = null;
            if (collectionTransaction.get().getQpayPaymentTransaction().size() > 0) {
                qpayPaymentTransaction = collectionTransaction.get().getQpayPaymentTransaction().get(collectionTransaction.get().getQpayPaymentTransaction().size() - 1);
            }
            return CollectionBillResponseDto.builder()
                    .billId(collectionTransaction.get().getId())
                    .amount(collectionTransaction.get().getAmountWithinDueDate())
                    .chargedAmount(qpayPaymentTransaction == null ? null : qpayPaymentTransaction.getAmount())
                    .amountAfterDueDate(collectionTransaction.get().getAmountAfterDueDate())
                    .dueDate(new Timestamp(collectionTransaction.get().getDueDate().getTime()))
                    .collectionTransactionId(collectionTransaction.get().getServiceTransactionId())
                    .transactionId(collectionTransaction.get().getTransactionStamp())
                    .userName(collectionTransaction.get().getConsumer().getName())
                    .billStatus(collectionTransaction.get().getBillStatus())
                    .consumerId(collectionTransaction.get().getConsumer().getId().toString())
                    .consumerEmail(collectionTransaction.get().getConsumer().getEmail())
                    .billingMonth(collectionTransaction.get().getBillingMonth())
                    .paymentStatus(qpayPaymentTransaction == null ? null : qpayPaymentTransaction.getPaymentStatus())
                    .gatewayType(qpayPaymentTransaction == null ? null : qpayPaymentTransaction.getGateway())
                    .paidAt(qpayPaymentTransaction == null ? null : qpayPaymentTransaction.getUpdatedAt())
                    .build();
        } else {
            log.error(BillErrorType.ENABLE_TO_GET_BILL.getErrorMessage());
            throw new ServiceException(BillErrorType.ENABLE_TO_GET_BILL);
        }
    }

    @Override
    public CollectionTransaction save(CollectionTransaction collectionTransaction) {
        return collectionTransactionRepository.save(collectionTransaction);
    }
}
