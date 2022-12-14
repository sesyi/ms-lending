package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.internal.request.CollectionBillRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.QpayCollectionRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayCollectionResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayLinkResponseDto;
import com.qisstpay.lendingservice.dto.qpay.request.MetadataRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.NiftOtpRequestDto;
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
import com.qisstpay.lendingservice.error.errortype.PaymentErrorType;
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
        if(collectionTransaction.get().getBillStatus().equals(BillStatusType.PAID)){
            return QpayCollectionResponseDto.builder()
                    .authorizedPayment(Boolean.FALSE)
                    .gateway(collectionRequestDto.getGateway())
                    .status("0000")
                    .source("")
                    .furtherAction(Boolean.FALSE)
                    .redirectURL("")
                    .billId(collectionTransaction.get().getId())
                    .billStatus(collectionTransaction.get().getBillStatus())
                    .transactionId(collectionTransaction.get().getServiceTransactionId())
                    .message("Bill Already Paid")
                    .build();
        }
        if (!collectionRequestDto.getGateway().equals(PaymentGatewayType.STRIPE)) {
            Optional<ConsumerAccount> account = consumerAccountService.geByAccountNumOrIBAN(collectionRequestDto.getAccountNumber());
            if (account.isEmpty()) {
                account = Optional.ofNullable(
                        consumerAccountService.createAccount(
                                collectionRequestDto.getAccountNumber(),
                                bankService.getByCode(collectionRequestDto.getBankID()),
                                collectionTransaction.get().getConsumer()));
            }
            if (collectionTransaction.get().getConsumer().getEmail() == null) {
                collectionTransaction.get().getConsumer().setEmail(collectionRequestDto.getCustomerEmail());
            }
        }
        callLog.setUser(collectionTransaction.get().getLenderCall().getUser());
        QpayPaymentResponseDto qpayPaymentResponseDto = qpayPaymentService.payment(
                getPaymentPayload(collectionRequestDto, collectionTransaction.get(), callLog),
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

    private QpayPaymentRequestDto getPaymentPayload(QpayCollectionRequestDto collectionRequestDto, CollectionTransaction collectionTransaction, LenderCallLog callLog) {
        String transactionId = String.format("qpay-%s-%s-%s", collectionTransaction.getLenderCall().getUser().getId(), collectionTransaction.getId(), callLog.getId());
        String refTransactionId = String.format("ref-qpay-%s-%s-%s", collectionTransaction.getLenderCall().getUser().getId(), collectionTransaction.getId(), callLog.getId());
        QpayPaymentRequestDto paymentRequestDto = null;
        if (collectionRequestDto.getGateway().equals(PaymentGatewayType.EASYPAISA)) {
            paymentRequestDto = QpayPaymentRequestDto.builder()
                    .accountNumber(collectionRequestDto.getAccountNumber())
                    .customerName(collectionTransaction.getConsumer().getName())
                    .amount(collectionTransaction.getDueDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0 ? collectionTransaction.getAmount() : collectionTransaction.getAmountAfterDueDate())
                    .country("PK")
                    .currency("PKR")
                    .customerEmail(collectionRequestDto.getCustomerEmail())
                    .gateway(collectionRequestDto.getGateway().getName())
                    .transactionId(transactionId)
                    .taxAmount(0.0)
                    .gatewayCredentials(new HashMap<>())
                    .build();
        } else if (collectionRequestDto.getGateway().equals(PaymentGatewayType.STRIPE)) {
            paymentRequestDto = QpayPaymentRequestDto.builder()
                    .accountNumber(collectionRequestDto.getAccountNumber())
                    .customerName(collectionTransaction.getConsumer().getName())
                    .amount(collectionTransaction.getDueDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0 ? collectionTransaction.getAmount() : collectionTransaction.getAmountAfterDueDate())
                    .country("PK")
                    .currency("PKR")
                    .locale("en-us")
                    .customerEmail(collectionRequestDto.getCustomerEmail())
                    .gateway(collectionRequestDto.getGateway().getName())
                    .threeDs(Boolean.TRUE.toString())
                    .cardHolderName(collectionRequestDto.getCardHolderName())
                    .cardNumber(collectionRequestDto.getCardNumber())
                    .cvv(collectionRequestDto.getCvv())
                    .expiryYear(collectionRequestDto.getExpiryYear())
                    .expiryMonth(collectionRequestDto.getExpiryMonth())
                    .transactionId(transactionId)
                    .taxAmount(0.0)
                    .source("card")
                    .installments(1)
                    .tokenizedCard(Boolean.FALSE.toString())
                    .gatewayCredentials(new HashMap<>())
                    .shippingAddress(new HashMap<>())
                    .build();
        } else if (collectionRequestDto.getGateway().equals(PaymentGatewayType.NIFT)) {
            paymentRequestDto = QpayPaymentRequestDto.builder()
                    .accountNumber(collectionRequestDto.getAccountNumber())
                    .customerName(collectionTransaction.getConsumer().getName())
                    .amount(collectionTransaction.getDueDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0 ? collectionTransaction.getAmount() : collectionTransaction.getAmountAfterDueDate())
                    .country("PK")
                    .currency("PKR")
                    .locale("en-us")
                    .customerEmail(collectionRequestDto.getCustomerEmail())
                    .gateway(collectionRequestDto.getGateway().getName())
                    .refTransactionId(refTransactionId)
                    .taxAmount(0.0)
                    .source("card")
                    .installments(1)
                    .gatewayCredentials(new HashMap<>())
                    .niftOtp(NiftOtpRequestDto.builder()
                            .cnic(collectionRequestDto.getCnic())
                            .accountNo(collectionRequestDto.getAccountNumber())
                            .bankID(collectionRequestDto.getBankID()).build())
                    .build();
        }
        return paymentRequestDto;

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
        if (collectionTransaction.get().getTransactionState().equals(TransactionState.RECEIVED)) {
            log.info(PaymentErrorType.ENABLE_TO_GET_STATUS.getErrorMessage());
            throw new ServiceException(PaymentErrorType.ENABLE_TO_GET_STATUS);
        }
        if (!gatewayType.equals(PaymentGatewayType.EASYPAISA) && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
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
            String statusUrl = String.format("/%s?gateway=%s", collectionTransaction.get().getServiceTransactionId(), gatewayType.getName());
            status = qpayPaymentService.status(statusUrl, callLog);
            if (status.getGatewayResponse().getGateway().equals(PaymentGatewayType.EASYPAISA.getName())) {
                if (status.getGatewayResponse().getGatewayMessage().equals("PAID") && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
                    collectionTransaction.get().setBillStatus(BillStatusType.PAID);
                    collectionTransaction.get().setTransactionState(TransactionState.COMPLETED);
                    collectionTransactionService.save(collectionTransaction.get());
                }
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
