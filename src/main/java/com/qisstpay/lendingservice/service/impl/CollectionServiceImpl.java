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
import com.qisstpay.lendingservice.dto.qpay.request.MetadataRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.NiftOtpRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.QpayCaptureRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.QpayPaymentRequestDto;
import com.qisstpay.lendingservice.dto.qpay.response.QpayPaymentResponseDto;
import com.qisstpay.lendingservice.entity.CollectionTransaction;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerAccount;
import com.qisstpay.lendingservice.entity.EPCallLog;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.LendingTransaction;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.AbroadResponseCode;
import com.qisstpay.lendingservice.enums.BillStatusType;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.CallType;
import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import com.qisstpay.lendingservice.enums.QPResponseCode;
import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.enums.TransactionState;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private ConsumerService consumerService;

    @Autowired
    private CollectionTransactionService collectionTransactionService;

    @Autowired
    private LendingService lendingService;

    @Autowired
    private CollectionTransactionRepository collectionTransactionRepository;

    private final String CALLING_SERVICE = "Calling Collection Service";

    private final String qpayUrl = "%s/?bid=%s";

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SUCCESS_STATUS_CODE = "00";

    @Value("${qpay.payment-link-base-url}")
    private String paymentURL;

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
            if (status.getGatewayResponse().getGatewayMessage().equals("PAID") && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
                collectionTransaction.get().setBillStatus(BillStatusType.PAID);
                collectionTransaction.get().setTransactionState(TransactionState.COMPLETED);
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

    @Override
    public EPCollectionInquiryResponse billInquiry(EPCollectionInquiryRequest epCollectionInquiryRequest, EPCallLog savedEpCallLog) throws ParseException {
        log.info("Inquiry method has been invoked in CollectionServiceImpl class...");

        if (StringUtils.isBlank(epCollectionInquiryRequest.getConsumerNumber())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "consumer number is missing.");
        }

        Optional<Consumer> consumer = consumerService.findByConsumerNumber(epCollectionInquiryRequest.getConsumerNumber());
        if (!consumer.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "consumer is not found.");
        }

        if (StringUtils.isBlank(epCollectionInquiryRequest.getBankMnemonic())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "lender ucid is missing.");
        }

        Optional<User> lender = userService.getUserByUcid(epCollectionInquiryRequest.getBankMnemonic());
        if (!lender.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "lender is not found.");
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
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), abroadBaseUrl+billInquiryUrl, new HttpEntity<>(abroadInquiryRequest), environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
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
                    .responseCode(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getCode())
                    .responseMessage(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getDescription())
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
        savedCollectionTransaction.setAmount(Double.valueOf(abroadInquiryResponse.getAmountPaid()));
        savedCollectionTransaction.setAmountWithinDueDate(Double.valueOf(abroadInquiryResponse.getAmountWithinDueDate()));
        savedCollectionTransaction.setBillingMonth(abroadInquiryResponse.getBillingMonth());
        savedCollectionTransaction.setBillStatus(abroadInquiryResponse.getBillStatus() == "U"? BillStatusType.UNPAID : BillStatusType.PAID);
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
                .build();
    }

    @Override
    public EPCollectionBillUpdateResponse billUpdate(EPCollectionBillUpdateRequest epCollectionBillUpdateRequest, EPCallLog savedEpCallLog) throws ParseException {
        log.info("Update method has been invoked in CollectionServiceImpl class...");

        if (StringUtils.isBlank(epCollectionBillUpdateRequest.getConsumerNumber())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "consumer number is missing.");
        }

        Optional<Consumer> consumer = consumerService.findByConsumerNumber(epCollectionBillUpdateRequest.getConsumerNumber());
        if (!consumer.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "consumer is not found.");
        }

        if (StringUtils.isBlank(epCollectionBillUpdateRequest.getBankMnemonic())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "lender ucid is missing.");
        }

        Optional<User> lender = userService.getUserByUcid(epCollectionBillUpdateRequest.getBankMnemonic());
        if (!lender.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "lender is not found.");
        }

        Optional<CollectionTransaction> collectionTransaction = collectionTransactionRepository.findByConsumerAndTransactionStateOrderByCreatedAtDesc(consumer.get(), TransactionState.INQUIRY_SUCCESS);
        if (!collectionTransaction.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "Inquiry is not done, pls perform inquiry first.");
        }

        // persist collection transaction
        collectionTransaction.get().setConsumer(consumer.get());
        collectionTransaction.get().setServiceTransactionId(epCollectionBillUpdateRequest.getTranAuthId());
        collectionTransaction.get().setDatePaid(getDateFromString(epCollectionBillUpdateRequest.getTranDate()));
        collectionTransaction.get().setAmount(Double.valueOf(epCollectionBillUpdateRequest.getTransactionAmount()));
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
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), abroadBaseUrl+billUpdateUrl, new HttpEntity<>(abroadBillUpdateRequest), environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }

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
                    .responseCode(AbroadResponseCode.ABROAD_BILL_UPDATE_FAILED.getCode())
                    .responseMessage(AbroadResponseCode.ABROAD_BILL_UPDATE_FAILED.getDescription())
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
