package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.enums.SlackTagType;
import com.qisstpay.commons.error.errortype.CommunicationErrorType;
import com.qisstpay.commons.exception.CustomException;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.Abroad.AbroadBillUpdateRequest;
import com.qisstpay.lendingservice.dto.Abroad.AbroadBillUpdateResponse;
import com.qisstpay.lendingservice.dto.Abroad.AbroadInquiryRequest;
import com.qisstpay.lendingservice.dto.Abroad.AbroadInquiryResponse;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionBillUpdateRequest;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionBillUpdateResponse;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionInquiryResponse;
import com.qisstpay.lendingservice.dto.internal.request.CollectionBillRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.QpayCollectionRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayCollectionResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.QpayLinkResponseDto;
import com.qisstpay.lendingservice.dto.qpay.request.*;
import com.qisstpay.lendingservice.dto.qpay.response.QpayPaymentResponseDto;
import com.qisstpay.lendingservice.entity.*;
import com.qisstpay.lendingservice.enums.*;
import com.qisstpay.lendingservice.error.errortype.PaymentErrorType;
import com.qisstpay.lendingservice.repository.CollectionTransactionRepository;
import com.qisstpay.lendingservice.service.*;
import com.qisstpay.lendingservice.utils.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private ConsumerService consumerService;

    @Autowired
    private CollectionTransactionService collectionTransactionService;

    @Autowired
    private LendingService lendingService;

    @Autowired
    private CollectionTransactionRepository collectionTransactionRepository;

    private ResourceBundle responses;

    private final String CALLING_SERVICE = "Calling Collection Service";

    private final String qpayUrl = "%s/?bid=%s";

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SUCCESS_STATUS_CODE = "00";

    @Value("${qpay.payment-link-base-url}")
    private String paymentURL;

    @Value("${qpay.callback-url}")
    private String callbackUrl;

    @Value("${abroad.endpoints.base-url}")
    private String abroadBaseUrl;

    @Value("${abroad.endpoints.inquiry}")
    private String billInquiryUrl;

    @Value("${abroad.endpoints.bill-update}")
    private String billUpdateUrl;

    @Value("${abroad.auth.access-key}")
    private String accessKey;

    @Value("${abroad.auth.access-key-value}")
    private String accessKeyValue;

    @Value("${environment}")
    private String environment;

    @Value("${message.slack.channel.third-party-errors}")
    private String thirdPartyErrorsSlackChannel;

    @Override
    public QpayCollectionResponseDto collectTroughQpay(QpayCollectionRequestDto collectionRequestDto, LenderCallLog callLog) {
        log.info(CALLING_SERVICE);
        log.info("In collectTroughQpay");
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionService.geById(collectionRequestDto.getBillId());
        if (collectionTransaction.get().getBillStatus().equals(BillStatusType.PAID)) {
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
                    .message(PaymentErrorType.BILL_PAID.getErrorMessage())
                    .build();
        }
        Optional<ConsumerAccount> account = Optional.empty();
        if (collectionRequestDto.getGateway().equals(PaymentGatewayType.EASYPAISA) || collectionRequestDto.getGateway().equals(PaymentGatewayType.NIFT)) {
            account = consumerAccountService.geByAccountNumOrIBAN(collectionRequestDto.getAccountNumber());
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
        QpayPaymentTransaction qpayPaymentTransaction = qpayPaymentService.payment(
                getPaymentPayload(collectionRequestDto, collectionTransaction.get(), callLog),
                callLog, collectionTransaction.get(), account, collectionRequestDto.getGateway());
        return QpayCollectionResponseDto.builder()
                .authorizedPayment(qpayPaymentTransaction.getAuthorizedPayment())
                .gateway(collectionRequestDto.getGateway())
                .status(qpayPaymentTransaction.getGatewayStatus())
                .source(qpayPaymentTransaction.getGatewaySource())
                .furtherAction(qpayPaymentTransaction.getFurtherAction())
                .redirectURL(qpayPaymentTransaction.getRedirectURL())
                .htmlSnippet(qpayPaymentTransaction.getHtmlSnippet())
                .billId(collectionTransaction.get().getId())
                .billStatus(collectionTransaction.get().getBillStatus())
                .transactionId(collectionTransaction.get().getServiceTransactionId())
                .message(qpayPaymentTransaction.getGatewayMessage())
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
                    .amount(collectionTransaction.getDueDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0 ? collectionTransaction.getAmountWithinDueDate() : collectionTransaction.getAmountAfterDueDate())
                    .country("PK")
                    .currency("PKR")
                    .customerEmail(collectionRequestDto.getCustomerEmail())
                    .gateway(collectionRequestDto.getGateway().getName())
                    .transactionId(transactionId)
                    .taxAmount(0.0)
                    .gatewayCredentials(new HashMap<>())
                    .build();
        }
//        else if (collectionRequestDto.getGateway().equals(PaymentGatewayType.STRIPE)) {
//            paymentRequestDto = QpayPaymentRequestDto.builder()
//                    .accountNumber(collectionRequestDto.getAccountNumber())
//                    .customerName(collectionTransaction.getConsumer().getName())
//                    .amount(collectionTransaction.getDueDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0 ? collectionTransaction.getAmount() : collectionTransaction.getAmountAfterDueDate())
//                    .country("PK")
//                    .currency("PKR")
//                    .locale("en-us")
//                    .customerEmail(collectionRequestDto.getCustomerEmail())
//                    .gateway(collectionRequestDto.getGateway().getName())
//                    .threeDs(Boolean.TRUE.toString())
//                    .cardHolderName(collectionRequestDto.getCardHolderName())
//                    .cardNumber(collectionRequestDto.getCardNumber())
//                    .cvv(collectionRequestDto.getCvv())
//                    .expiryYear(collectionRequestDto.getExpiryYear())
//                    .expiryMonth(collectionRequestDto.getExpiryMonth())
//                    .transactionId(transactionId)
//                    .taxAmount(0.0)
//                    .source("card")
//                    .installments(1)
//                    .tokenizedCard(Boolean.FALSE.toString())
//                    .gatewayCredentials(new HashMap<>())
//                    .shippingAddress(new HashMap<>())
//                    .build();
//        }
        else if (collectionRequestDto.getGateway().equals(PaymentGatewayType.ALFALAH)) {
            paymentRequestDto = QpayPaymentRequestDto.builder()
                    .accountNumber(collectionRequestDto.getAccountNumber())
                    .customerName(collectionTransaction.getConsumer().getName())
                    .amount(collectionTransaction.getDueDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0 ? collectionTransaction.getAmountWithinDueDate() : collectionTransaction.getAmountAfterDueDate())
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
                    .refTransactionId(refTransactionId)
                    .taxAmount(0.0)
                    .source("card")
                    .ipAddress(collectionRequestDto.getIpAddress())
                    .installments(1)
                    .tokenizedCard(Boolean.FALSE.toString())
                    .sourceMetadata(new SourceMetadataRequestDto(callbackUrl))
                    .gatewayCredentials(new HashMap<>())
                    .shippingAddress(new HashMap<>())
                    .build();
        } else if (collectionRequestDto.getGateway().equals(PaymentGatewayType.NIFT)) {
            paymentRequestDto = QpayPaymentRequestDto.builder()
                    .accountNumber(collectionRequestDto.getAccountNumber())
                    .customerName(collectionTransaction.getConsumer().getName())
                    .amount(collectionTransaction.getDueDate().compareTo(new Timestamp(System.currentTimeMillis())) > 0 ? collectionTransaction.getAmountWithinDueDate() : collectionTransaction.getAmountAfterDueDate())
                    .country("PK")
                    .currency("PKR")
                    .locale("en-us")
                    .customerEmail(collectionRequestDto.getCustomerEmail())
                    .gateway(collectionRequestDto.getGateway().getName())
                    .refTransactionId(refTransactionId)
                    .transactionId(transactionId)
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
            Optional<LendingTransaction> lendingTransaction = lendingTransactionService.geByTransactionStamp(billRequestDto.getTransactionId(),lenderCallLog.getUser().getId());
            lenderCallLog.setStatus(CallStatusType.SUCCESS);
            CollectionTransaction collectionTransaction = collectionTransactionService.save(CollectionTransaction.builder()
                    .amountWithinDueDate(billRequestDto.getAmount())
                    .amountAfterDueDate(billRequestDto.getAmountAfterDueDate())
                    .consumer(lendingTransaction.get().getConsumer())
                    .dueDate(billRequestDto.getDueDate())
                    .billingMonth(billRequestDto.getBillingMonth())
                    .billStatus(BillStatusType.UNPAID)
                    .transactionState(TransactionState.RECEIVED)
                    .lenderCall(lenderCallLog)
                    .transactionStamp(billRequestDto.getTransactionId())
                    .qpayPaymentTransaction(new ArrayList<>())
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

    public QpayCollectionResponseDto qpayCollectionStatus(Long billId, LenderCallLog callLog, String otp) {
        log.info(CALLING_SERVICE);
        log.info("In collectTroughQpay");
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionService.geById(billId);
        return qpayCollectionStatus(collectionTransaction.get(), callLog, otp);
    }

    @Override
    public QpayCollectionResponseDto qpayCollectionStatus(CollectionTransaction collectionTransaction, LenderCallLog callLog, String otp) {
        log.info(CALLING_SERVICE);
        log.info("In collectTroughQpay");
        if (collectionTransaction.getTransactionState().equals(TransactionState.RECEIVED)) {
            log.info(PaymentErrorType.ENABLE_TO_GET_STATUS.getErrorMessage());
            throw new ServiceException(PaymentErrorType.ENABLE_TO_GET_STATUS);
        }
        QpayPaymentTransaction qpayPaymentTransaction = collectionTransaction.getQpayPaymentTransaction().get(collectionTransaction.getQpayPaymentTransaction().size() > 1 ? collectionTransaction.getQpayPaymentTransaction().size() - 1 : 0);
        if (!qpayPaymentTransaction.getGateway().equals(PaymentGatewayType.EASYPAISA) && collectionTransaction.getBillStatus().equals(BillStatusType.UNPAID)) {
            QpayPaymentResponseDto capture = qpayPaymentService.capture(
                    getCaptureRequestPayload(otp, qpayPaymentTransaction, collectionTransaction), callLog);
            if (capture.getSuccess().equals(Boolean.TRUE)) {
                qpayPaymentTransaction.setAuthorizedPayment(capture.getGatewayResponse().getAuthorizedPayment());
                qpayPaymentTransaction.setRedirectURL(capture.getRedirectURL());
                qpayPaymentTransaction.setHtmlSnippet(capture.getHtmlSnippet());
                qpayPaymentTransaction.setGatewayCode(capture.getGatewayResponse().getGatewayCode());
                qpayPaymentTransaction.setGatewayStatus(capture.getGatewayResponse().getGatewayStatus());
                qpayPaymentTransaction.setGatewayMessage(capture.getGatewayResponse().getGatewayMessage());
                qpayPaymentTransaction.setPaymentStatus(capture.getGatewayResponse().getPaymentStatus());
                if (capture.getGatewayResponse().getPaymentStatus().equals("Complete") && collectionTransaction.getBillStatus().equals(BillStatusType.UNPAID)) {
                    collectionTransaction.setBillStatus(BillStatusType.PAID);
                    collectionTransaction.setTransactionState(TransactionState.COMPLETED);
                }
            }
            collectionTransactionService.save(collectionTransaction);
        }
        if (!qpayPaymentTransaction.getGateway().equals(PaymentGatewayType.NIFT) && collectionTransaction.getBillStatus().equals(BillStatusType.UNPAID)) {
            String statusUrl = String.format("/%s?gateway=%s", collectionTransaction.getServiceTransactionId(), qpayPaymentTransaction.getGateway().getName());
            QpayPaymentResponseDto status = qpayPaymentService.status(statusUrl, callLog);
            qpayPaymentTransaction.setAuthorizedPayment(status.getGatewayResponse().getAuthorizedPayment());
            qpayPaymentTransaction.setRedirectURL(status.getRedirectURL());
            qpayPaymentTransaction.setHtmlSnippet(status.getHtmlSnippet());
            qpayPaymentTransaction.setGatewayCode(status.getGatewayResponse().getGatewayCode());
            qpayPaymentTransaction.setGatewayStatus(status.getGatewayResponse().getGatewayStatus());
            qpayPaymentTransaction.setGatewayMessage(status.getGatewayResponse().getGatewayMessage());
            qpayPaymentTransaction.setPaymentStatus(status.getGatewayResponse().getPaymentStatus());
            if (status.getGatewayResponse().getPaymentStatus().equals("Complete") && collectionTransaction.getBillStatus().equals(BillStatusType.UNPAID)) {
                collectionTransaction.setBillStatus(BillStatusType.PAID);
                collectionTransaction.setTransactionState(TransactionState.COMPLETED);
            } else if (collectionTransaction.getBillStatus().equals(BillStatusType.UNPAID)) {
                collectionTransaction.setBillStatus(BillStatusType.UNPAID);
                collectionTransaction.setTransactionState(TransactionState.FAILURE);
            }
            collectionTransactionService.save(collectionTransaction);
        }
        return QpayCollectionResponseDto.builder()
                .authorizedPayment(qpayPaymentTransaction.getAuthorizedPayment())
                .gateway(qpayPaymentTransaction.getGateway())
                .status(qpayPaymentTransaction.getGatewayStatus())
                .source(qpayPaymentTransaction.getGatewaySource())
                .furtherAction(qpayPaymentTransaction.getFurtherAction())
                .redirectURL(qpayPaymentTransaction.getRedirectURL())
                .billId(collectionTransaction.getId())
                .billStatus(collectionTransaction.getBillStatus())
                .transactionId(collectionTransaction.getServiceTransactionId())
                .message(qpayPaymentTransaction.getGatewayMessage())
                .paymentStatus(qpayPaymentTransaction.getPaymentStatus())
                .build();

    }

    private QpayCaptureRequestDto getCaptureRequestPayload(String otp, QpayPaymentTransaction qpayPaymentTransaction, CollectionTransaction collectionTransaction) {
        if (qpayPaymentTransaction.getGateway().equals(PaymentGatewayType.NIFT)) {
            return QpayCaptureRequestDto.builder()
                    .metadata(MetadataRequestDto.builder()
                            .gatewayCredentials(new HashMap<>())
                            .gateway(qpayPaymentTransaction.getGateway().getName())
                            .niftTransaction(
                                    NiftTransactionRequestDto.builder()
                                            .otp(otp)
                                            .refTransactionId(qpayPaymentTransaction.getTransactionId())
                                            .bankId(qpayPaymentTransaction.getConsumerAccount().getBank().getCode())
                                            .serviceTransactionId(collectionTransaction.getServiceTransactionId())
                                            .build()).build())
                    .id("")
                    .build();
        } else if (qpayPaymentTransaction.getGateway().equals(PaymentGatewayType.ALFALAH)) {
            return QpayCaptureRequestDto.builder()
                    .metadata(MetadataRequestDto.builder()
                            .gatewayCredentials(new HashMap<>())
                            .gateway(qpayPaymentTransaction.getGateway().getName())
                            .amount(qpayPaymentTransaction.getAmount())
                            .source("capture")
                            .currency("PKR")
                            .transactionId(qpayPaymentTransaction.getTransactionId())
                            .refTransactionId(qpayPaymentTransaction.getRefTransactionId())
                            .serviceTransactionId(qpayPaymentTransaction.getGatewayClientSecret()).build())
                    .id(qpayPaymentTransaction.getGatewayCardSourceId())
                    .build();
        }
        return null;
    }

    @Override
    public EPCollectionInquiryResponse billInquiry(EPCollectionInquiryRequest epCollectionInquiryRequest, EPCallLog savedEpCallLog) throws ParseException {
        log.info(CALLING_SERVICE);
        log.info("Inquiry method has been invoked in CollectionServiceImpl class...");

        if (StringUtils.isBlank(epCollectionInquiryRequest.getConsumerNumber())) {
            log.error(HttpStatus.BAD_REQUEST.toString(), "consumer number is missing.");
            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(TransferState.INVALID_DATA_EP.getCode())
                    .responseMessage(TransferState.INVALID_DATA_EP.getState())
                    .status(TransferState.INVALID_DATA_EP.getDescription())
                    .build();
        }

        Optional<Consumer> consumer = consumerService.findByConsumerNumber(epCollectionInquiryRequest.getConsumerNumber());
        if (!consumer.isPresent()) {
            log.info("consumer is not found: {}", epCollectionInquiryRequest.getConsumerNumber());
            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(TransferState.CUSTOMER_NOT_FOUND.getCode())
                    .responseMessage(TransferState.CUSTOMER_NOT_FOUND.getState())
                    .status(TransferState.CUSTOMER_NOT_FOUND.getDescription())
                    .build();
        }

        if (StringUtils.isBlank(epCollectionInquiryRequest.getBankMnemonic())) {
            log.error(HttpStatus.BAD_REQUEST.toString(), "lender ucid is missing.");
            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(TransferState.INVALID_DATA_EP.getCode())
                    .responseMessage(TransferState.INVALID_DATA_EP.getState())
                    .status(TransferState.INVALID_DATA_EP.getDescription())
                    .build();
        }

        Optional<User> lender = userService.getUserByUcid(epCollectionInquiryRequest.getBankMnemonic());
        if (!lender.isPresent()) {
            log.info("lender is not found with ucid: {}", epCollectionInquiryRequest.getBankMnemonic());
            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(TransferState.INVALID_DATA_EP.getCode())
                    .responseMessage(TransferState.INVALID_DATA_EP.getState())
                    .status(TransferState.INVALID_DATA_EP.getDescription())
                    .build();
        }

        // persist collection transaction
        CollectionTransaction collectionTransaction = CollectionTransaction.builder()
                .consumer(consumer.get())
                .transactionState(TransactionState.RECEIVED)
                .epCallLog(savedEpCallLog)
                .build();
        CollectionTransaction savedCollectionTransaction = collectionTransactionService.save(collectionTransaction);

        AbroadInquiryRequest abroadInquiryRequest = AbroadInquiryRequest.builder()
                .consumerNumber(epCollectionInquiryRequest.getConsumerNumber())
                .build();

        // add lender call log
        LenderCallLog savedLenderCallLog = lendingCallService.saveLenderCall(
                lender.get(),
                abroadInquiryRequest.toString(),
                ServiceType.EP,
                CallType.SENT);

        AbroadInquiryResponse abroadInquiryResponse;
        try {
            abroadInquiryResponse = abroadBillInquiryCall(abroadInquiryRequest);
        } catch (Exception e) {
            log.error("Exception Occurred in Abroad Inquiry for consumer: {}", epCollectionInquiryRequest.getConsumerNumber());
            lendingService.updateEpCallLog(savedEpCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null, savedCollectionTransaction);
            lendingService.updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.ABROAD_INQUIRY_FAILED.getDescription(), savedLenderCallLog);
            savedCollectionTransaction.setTransactionState(TransactionState.EXCEPTION);
            collectionTransactionService.save(savedCollectionTransaction);
            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(TransferState.UNKNOWN_ERROR.getCode())
                    .responseMessage(TransferState.UNKNOWN_ERROR.getState())
                    .status(TransferState.UNKNOWN_ERROR.getDescription())
                    .build();
        }
        if (!abroadInquiryResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

            //  update ep call log
            lendingService.updateEpCallLog(
                    savedEpCallLog,
                    CallStatusType.FAILURE,
                    abroadInquiryResponse.getResponseCode(),
                    null,
                    abroadInquiryResponse.toString(),
                    savedCollectionTransaction);

            //  update lender call log
            lendingService.updateLenderCallLog(
                    CallStatusType.FAILURE,
                    QPResponseCode.ABROAD_INQUIRY_FAILED.getDescription(),
                    savedLenderCallLog);

            // update collection transaction
            savedCollectionTransaction.setTransactionState(TransactionState.FAILURE);
            collectionTransactionService.save(savedCollectionTransaction);

            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(abroadInquiryResponse.getResponseCode())
                    .responseMessage(abroadInquiryResponse.getResponseMessage())
                    .status(abroadInquiryResponse.getStatus())
                    .build();
        }

        //  update ep call log
        lendingService.updateEpCallLog(
                savedEpCallLog,
                CallStatusType.SUCCESS,
                abroadInquiryResponse.getResponseCode(),
                null,
                abroadInquiryResponse.toString(),
                savedCollectionTransaction);

//        //  update ep call log
        lendingService.updateLenderCallLog(
                CallStatusType.SUCCESS,
                QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(),
                savedLenderCallLog);

        // update collection transaction
        savedCollectionTransaction.setTransactionState(TransactionState.INQUIRY_SUCCESS);
        savedCollectionTransaction.setAmountAfterDueDate(Double.valueOf(abroadInquiryResponse.getAmountAfterDueDate()));
        savedCollectionTransaction.setAmountCollected(Double.valueOf(abroadInquiryResponse.getAmountPaid()));
        savedCollectionTransaction.setAmountWithinDueDate(Double.valueOf(abroadInquiryResponse.getAmountWithinDueDate()));
        savedCollectionTransaction.setBillingMonth(abroadInquiryResponse.getBillingMonth());
        savedCollectionTransaction.setBillStatus(abroadInquiryResponse.getBillStatus() == "U" ? BillStatusType.UNPAID : BillStatusType.PAID);
        savedCollectionTransaction.setServiceTransactionId(abroadInquiryResponse.getTranAuthId());
        savedCollectionTransaction.setConsumerName(abroadInquiryResponse.getConsumerName());
        savedCollectionTransaction.setDatePaid(getDateFromString(abroadInquiryResponse.getDatePaid()));
        savedCollectionTransaction.setDueDate(getDateFromString(abroadInquiryResponse.getDueDate()));
        collectionTransactionService.save(savedCollectionTransaction);

        return EPCollectionInquiryResponse
                .builder()
                .responseCode(abroadInquiryResponse.getResponseCode())
                .amountAfterDueDate(abroadInquiryResponse.getAmountAfterDueDate())
                .amountPaid(abroadInquiryResponse.getAmountPaid())
                .amountWithinDueDate(abroadInquiryResponse.getAmountWithinDueDate())
                .billingMonth(abroadInquiryResponse.getBillingMonth())
                .billStatus(abroadInquiryResponse.getBillStatus())
                .consumerName(abroadInquiryResponse.getConsumerName())
                .datePaid(abroadInquiryResponse.getDatePaid())
                .dueDate(abroadInquiryResponse.getDueDate())
                .tranAuthId(abroadInquiryResponse.getTranAuthId())
                .reserved(abroadInquiryResponse.getReserved())
                .build();
    }

    @Override
    public EPCollectionBillUpdateResponse billUpdate(EPCollectionBillUpdateRequest epCollectionBillUpdateRequest, EPCallLog savedEpCallLog) throws ParseException {
        log.info(CALLING_SERVICE);
        log.info("Update method has been invoked in CollectionServiceImpl class...");
        if (StringUtils.isBlank(epCollectionBillUpdateRequest.getConsumerNumber())) {
            log.error(HttpStatus.BAD_REQUEST.toString(), "consumer number is missing.");
            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(TransferState.INVALID_DATA_EP.getCode())
                    .identificationParameter(TransferState.INVALID_DATA_EP.getState())
                    .reserved(TransferState.INVALID_DATA_EP.getDescription())
                    .build();
        }

        Optional<Consumer> consumer = consumerService.findByConsumerNumber(epCollectionBillUpdateRequest.getConsumerNumber());
        if (!consumer.isPresent()) {
            log.info("Consumer is not found: {}", epCollectionBillUpdateRequest.getConsumerNumber());
            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(TransferState.CUSTOMER_NOT_FOUND.getCode())
                    .identificationParameter(TransferState.CUSTOMER_NOT_FOUND.getState())
                    .reserved(TransferState.CUSTOMER_NOT_FOUND.getDescription())
                    .build();
        }

        if (StringUtils.isBlank(epCollectionBillUpdateRequest.getBankMnemonic())) {
            log.error(HttpStatus.BAD_REQUEST.toString(), "lender ucid is missing.");
            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(TransferState.INVALID_DATA_EP.getCode())
                    .identificationParameter(TransferState.INVALID_DATA_EP.getState())
                    .reserved(TransferState.INVALID_DATA_EP.getDescription())
                    .build();
        }

        Optional<User> lender = userService.getUserByUcid(epCollectionBillUpdateRequest.getBankMnemonic());
        if (!lender.isPresent()) {
            log.info("lender is not found with ucid: {}", epCollectionBillUpdateRequest.getBankMnemonic());
            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(TransferState.INVALID_DATA_EP.getCode())
                    .identificationParameter(TransferState.INVALID_DATA_EP.getState())
                    .reserved(TransferState.INVALID_DATA_EP.getDescription())
                    .build();
        }

        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findTopByConsumerAndTransactionStateOrderByCreatedAtDesc(consumer.get(), TransactionState.INQUIRY_SUCCESS);
        if (!collectionTransaction.isPresent()) {
            log.error(HttpStatus.BAD_REQUEST.toString(), "Either inquiry is not done, Or it has already been done.");
            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(TransferState.UNKNOWN_ERROR.getCode())
                    .identificationParameter(TransferState.UNKNOWN_ERROR.getState())
                    .reserved(TransferState.UNKNOWN_ERROR.getDescription())
                    .build();
        }

        // persist collection transaction
        collectionTransaction.get().setConsumer(consumer.get());
        collectionTransaction.get().setServiceTransactionId(epCollectionBillUpdateRequest.getTranAuthId());
        collectionTransaction.get().setDatePaid(getDateFromString(epCollectionBillUpdateRequest.getTranDate()));
        collectionTransaction.get().setAmountCollected(Double.valueOf(epCollectionBillUpdateRequest.getTransactionAmount()));
        collectionTransaction.get().setTransactionState(TransactionState.IN_PROGRESS);
        collectionTransaction.get().setEpCallLog(savedEpCallLog);
        CollectionTransaction savedCollectionTransaction = collectionTransactionService.save(collectionTransaction.get());

        AbroadBillUpdateRequest abroadBillUpdateRequest = AbroadBillUpdateRequest.builder()
                .consumerNumber(epCollectionBillUpdateRequest.getConsumerNumber())
                .tranAuthId(epCollectionBillUpdateRequest.getTranAuthId())
                .tranDate(epCollectionBillUpdateRequest.getTranDate())
                .transactionAmount(epCollectionBillUpdateRequest.getTransactionAmount())
                .tranTime(epCollectionBillUpdateRequest.getTranTime())
                .build();

        // add lender call log
        LenderCallLog savedLenderCallLog = lendingCallService.saveLenderCall(
                lender.get(),
                abroadBillUpdateRequest.toString(),
                ServiceType.EP,
                CallType.SENT);

        AbroadBillUpdateResponse abroadBillUpdateResponse;
        try {
            abroadBillUpdateResponse = abroadBillUpdateCall(abroadBillUpdateRequest);
        } catch (Exception e) {
            log.error("Exception Occurred in Abroad Bill Update for consumer: {}", epCollectionBillUpdateRequest.getConsumerNumber());
            lendingService.updateEpCallLog(savedEpCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null, savedCollectionTransaction);
            lendingService.updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.ABROAD_BILL_UPDATE_FAILED.getDescription(), savedLenderCallLog);
            savedCollectionTransaction.setTransactionState(TransactionState.EXCEPTION);
            collectionTransactionService.save(savedCollectionTransaction);
            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(TransferState.UNKNOWN_ERROR.getCode())
                    .identificationParameter(TransferState.UNKNOWN_ERROR.getState())
                    .reserved(TransferState.UNKNOWN_ERROR.getDescription())
                    .build();        }

        if (!abroadBillUpdateResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

            //  update ep call log
            lendingService.updateEpCallLog(
                    savedEpCallLog,
                    CallStatusType.FAILURE,
                    abroadBillUpdateResponse.getResponseCode(),
                    null,
                    abroadBillUpdateResponse.toString(),
                    savedCollectionTransaction);

            //  update lender call log
            lendingService.updateLenderCallLog(
                    CallStatusType.FAILURE,
                    QPResponseCode.ABROAD_BILL_UPDATE_FAILED.getDescription(),
                    savedLenderCallLog);

            // update collection transaction
            savedCollectionTransaction.setTransactionState(TransactionState.FAILURE);
            collectionTransactionService.save(savedCollectionTransaction);

            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(abroadBillUpdateResponse.getResponseCode())
                    .identificationParameter(abroadBillUpdateResponse.getIdentificationParameter())
                    .reserved(abroadBillUpdateResponse.getReserved())
                    .build();
        }

        //  update ep call log
        lendingService.updateEpCallLog(
                savedEpCallLog,
                CallStatusType.SUCCESS,
                abroadBillUpdateResponse.getResponseCode(),
                null,
                abroadBillUpdateResponse.toString(),
                savedCollectionTransaction);

//        //  update lender call log
        lendingService.updateLenderCallLog(
                CallStatusType.SUCCESS,
                QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(),
                savedLenderCallLog);

        // update collection transaction
        savedCollectionTransaction.setTransactionState(TransactionState.COMPLETED);
        savedCollectionTransaction.setIdentificationParameter(abroadBillUpdateResponse.getIdentificationParameter());
        savedCollectionTransaction.setReserved(abroadBillUpdateResponse.getReserved());
        savedCollectionTransaction.setBillStatus(BillStatusType.PAID);
        collectionTransactionService.save(savedCollectionTransaction);

        return EPCollectionBillUpdateResponse
                .builder()
                .responseCode(abroadBillUpdateResponse.getResponseCode())
                .identificationParameter(abroadBillUpdateResponse.getIdentificationParameter())
                .reserved(abroadBillUpdateResponse.getReserved())
                .build();
    }

    @Override
    public String qpayCallbackStatus(String orderId, String transactionId, String result, LenderCallLog callLog) {
        log.info(CALLING_SERVICE);
        log.info("In qpayCallbackStatus");
        if (result.equals("SUCCESS")) {
            Optional<CollectionTransaction> collectionTransaction = collectionTransactionService.geByServiceTransactionId(orderId);
            QpayCollectionResponseDto qpayCollectionResponseDto = qpayCollectionStatus(collectionTransaction.get(), callLog, "");
            if (qpayCollectionResponseDto.getPaymentStatus().equals("Complete")) {
                responses = ResourceBundle.getBundle("responses/Responses");
                return responses.getString("HTML-0001");
            }
            return qpayCollectionResponseDto.getPaymentStatus();
        }
        return "Payment Failed";
    }

    public AbroadInquiryResponse abroadBillInquiryCall(AbroadInquiryRequest abroadInquiryRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(accessKey, accessKeyValue);
        HttpEntity request = new HttpEntity(abroadInquiryRequest, headers);

        log.info("Abroad Bill Inquiry Request: {}", CommonUtility.getObjectJson(request));
        ResponseEntity<AbroadInquiryResponse> abroadInquiryResponse =
                restTemplate.exchange(abroadBaseUrl + billInquiryUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                });
        return abroadInquiryResponse.getBody();
    }

    public AbroadBillUpdateResponse abroadBillUpdateCall(AbroadBillUpdateRequest abroadBillUpdateRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(accessKey, accessKeyValue);
        HttpEntity request = new HttpEntity(abroadBillUpdateRequest, headers);

        log.info("Abroad Bill Update Request: {}", CommonUtility.getObjectJson(request));
        ResponseEntity<AbroadBillUpdateResponse> abroadBillUpdateResponse =
                restTemplate.exchange(abroadBaseUrl + billUpdateUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                });
        return abroadBillUpdateResponse.getBody();
    }

    private Date getDateFromString(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date convertedCurrentDate = sdf.parse("20130918");
        return convertedCurrentDate;
    }
}
