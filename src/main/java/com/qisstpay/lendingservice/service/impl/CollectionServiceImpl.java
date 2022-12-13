package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.internal.request.CollectionBillRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.QpayCollectionRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayCollectionResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayLinkResponseDto;
import com.qisstpay.lendingservice.dto.qpay.request.GatewayCredentialRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.MetadataRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.QpayCaptureRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.QpayPaymentRequestDto;
import com.qisstpay.lendingservice.dto.qpay.response.QpayPaymentResponseDto;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import com.qisstpay.lendingservice.entity.ConsumerAccount;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.LendingTransaction;
import com.qisstpay.lendingservice.enums.BillStatusType;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import com.qisstpay.lendingservice.enums.TransactionState;
import com.qisstpay.lendingservice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

@Service
@Slf4j
@RefreshScope
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private UserService userService;

    @Autowired
    private LendingTransactionService lendingTransactionService;

    @Autowired
    private ConsumerAccountService consumerAccountService;

    @Autowired
    private BankService bankService;

    @Autowired
    private QpayPaymentService qpayPaymentService;

    @Autowired
    private LendingCallService lendingCallService;

    @Autowired
    private CollectionTransactionService collectionTransactionService;

    @Value("${qpay.payment-link-base-url}")
    private String paymentURL;

    private final String CALLING_SERVICE = "Calling Collection Service";

    private final String qpayUrl = "%s/?bid=%s";

    @Override
    public QpayCollectionResponseDto collectTroughQpay(QpayCollectionRequestDto collectionRequestDto, LenderCallLog callLog) {
        log.info(CALLING_SERVICE);
        log.info("In collectTroughQpay");
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionService.geById(collectionRequestDto.getBillId());
        if (!collectionRequestDto.getGateway().equals(PaymentGatewayType.STRIP)) {
            Optional<ConsumerAccount> account = consumerAccountService.geByAccountNumOrIBAN(collectionRequestDto.getAccountNumber());
            if (account.isEmpty()) {
                account = Optional.ofNullable(
                        consumerAccountService.createAccount(
                                collectionRequestDto.getAccountNumber(),
                                bankService.getByCode(collectionRequestDto.getGateway().getCode()),
                                collectionTransaction.get().getConsumer()));
            }
            if (collectionTransaction.get().getConsumer().getEmail() == null) {
                collectionTransaction.get().getConsumer().setEmail(collectionRequestDto.getCustomerEmail());
            }
        }
        String transactionId = String.format("qpay-%s-%s-%s", collectionTransaction.get().getLenderCall().getUser().getId(), collectionTransaction.get().getId(), callLog.getId());
        callLog.setUser(collectionTransaction.get().getLenderCall().getUser());
        QpayPaymentResponseDto qpayPaymentResponseDto = qpayPaymentService.payment(
                QpayPaymentRequestDto.builder()
                        .accountNumber(collectionRequestDto.getAccountNumber())
                        .amount(collectionTransaction.get().getDueDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0 ? collectionTransaction.get().getAmount() : collectionTransaction.get().getAmountAfterDueDate())
                        .country("PK")
                        .currency("PKR")
                        .customerEmail(collectionRequestDto.getCustomerEmail())
                        .gateway(collectionRequestDto.getGateway().getName())
                        .gatewayCredentials(new HashMap<>())
                        .transactionId(transactionId)
                        .taxAmount(0.0)
                        .build(),
                callLog);
        collectionTransaction.get().setServiceTransactionId(qpayPaymentResponseDto.getGatewayResponse().getGatewayResponseId());
        collectionTransaction.get().setTransactionState(TransactionState.IN_PROGRESS);
        collectionTransactionService.save(collectionTransaction.get());
        return QpayCollectionResponseDto.builder()
                .authorizedPayment(qpayPaymentResponseDto.getGatewayResponse().getAuthorizedPayment())
                .gateway(collectionRequestDto.getGateway())
                .status(qpayPaymentResponseDto.getGatewayResponse().getGatewayStatus())
                .source(qpayPaymentResponseDto.getGatewayResponse().getGatewaySource())
                .furtherAction(qpayPaymentResponseDto.getFurtherAction())
                .redirectURL(qpayPaymentResponseDto.getRedirectURL())
                .billId(collectionTransaction.get().getId())
                .billStatus(collectionTransaction.get().getBillStatus())
                .transactionId(qpayPaymentResponseDto.getGatewayResponse().getGatewayResponseId())
                .message(qpayPaymentResponseDto.getGatewayResponse().getGatewayMessage())
                .build();
    }

    @Override
    public QpayLinkResponseDto getQpayLink(CollectionBillRequestDto billRequestDto, LenderCallLog lenderCallLog) {
        log.info(CALLING_SERVICE);
        log.info("In getQpayLink");
        try {
            Optional<LendingTransaction> lendingTransaction = lendingTransactionService.geByIdentityNumber(billRequestDto.getIdentityNumber());
            lenderCallLog.setStatus(CallStatusType.SUCCESS);
            CollectionTransaction collectionTransaction = collectionTransactionService.save(CollectionTransaction.builder()
                    .amount(billRequestDto.getAmount())
                    .amountAfterDueDate(billRequestDto.getAmountAfterDueDate())
                    .consumer(lendingTransaction.get().getConsumer())
                    .dueDate(billRequestDto.getDueDate())
                    .billStatus(BillStatusType.UNPAID)
                    .transactionState(TransactionState.RECEIVED)
                    .identityNumber(billRequestDto.getIdentityNumber())
                    .userName(billRequestDto.getUserName())
                    .lenderCall(lenderCallLog)
                    .build());
            return QpayLinkResponseDto.builder()
                    .message("Successfully generate link")
                    .qpayLink(String.format(qpayUrl, paymentURL, collectionTransaction.getId()))
                    .success(Boolean.TRUE).build();
        } catch (Exception exception) {
            lenderCallLog.setStatus(CallStatusType.EXCEPTION);
            lenderCallLog.setError(exception.getMessage());
            throw exception;
        } finally {
            lendingCallService.saveLenderCall(lenderCallLog);
        }
    }

    @Override
    public QpayCollectionResponseDto qpayCollectionStatus(Long billId, PaymentGatewayType gatewayType, LenderCallLog callLog) {
        log.info(CALLING_SERVICE);
        log.info("In collectTroughQpay");
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionService.geById(billId);
        QpayPaymentResponseDto capture;
        QpayPaymentResponseDto status;
        if (gatewayType.equals(PaymentGatewayType.STRIP) && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
            capture = qpayPaymentService.capture(
                    QpayCaptureRequestDto.builder()
                            .metadata(MetadataRequestDto.builder().gatewayCredentials(new HashMap<>()).gateway(gatewayType.getName()).build())
                            .transactionId(collectionTransaction.get().getServiceTransactionId())
                            .build(), callLog);
            if (capture.getSuccess().equals(Boolean.FALSE)) {
                return QpayCollectionResponseDto.builder()
                        .authorizedPayment(capture.getGatewayResponse().getAuthorizedPayment())
                        .gateway(gatewayType)
                        .status(capture.getGatewayResponse().getGatewayStatus())
                        .source(capture.getGatewayResponse().getGatewaySource())
                        .furtherAction(capture.getFurtherAction())
                        .redirectURL(capture.getRedirectURL())
                        .billId(collectionTransaction.get().getId())
                        .billStatus(collectionTransaction.get().getBillStatus())
                        .transactionId(capture.getGatewayResponse().getGatewayResponseId())
                        .message(capture.getGatewayResponse().getGatewayMessage())
                        .build();
            }
        }
        if (collectionTransaction.get().getServiceTransactionId() != null) {
            String statusUrl = String.format("/{}?gateway={}", collectionTransaction.get().getServiceTransactionId(), gatewayType.getName());
            status = qpayPaymentService.status(statusUrl, callLog);
            if (status.getGatewayResponse().getGatewayStatus().equals("succeeded") && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
                collectionTransaction.get().setBillStatus(BillStatusType.PAID);
                collectionTransaction.get().setTransactionState(TransactionState.IN_PROGRESS);
                collectionTransactionService.save(collectionTransaction.get());
            }
            return QpayCollectionResponseDto.builder()
                    .authorizedPayment(status.getGatewayResponse().getAuthorizedPayment())
                    .gateway(gatewayType)
                    .status(status.getGatewayResponse().getGatewayStatus())
                    .source(status.getGatewayResponse().getGatewaySource())
                    .furtherAction(status.getFurtherAction())
                    .redirectURL(status.getRedirectURL())
                    .billId(collectionTransaction.get().getId())
                    .billStatus(collectionTransaction.get().getBillStatus())
                    .transactionId(status.getGatewayResponse().getGatewayResponseId())
                    .message(status.getGatewayResponse().getGatewayMessage())
                    .build();
        }
        return QpayCollectionResponseDto.builder()
                .gateway(gatewayType)
                .billId(collectionTransaction.get().getId())
                .billStatus(collectionTransaction.get().getBillStatus())
                .build();
    }
}
