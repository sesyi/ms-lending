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
import java.util.ArrayList;
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
    private CollectionTransactionService collectionTransactionService;

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
        QpayPaymentTransaction qpayPaymentTransaction = new QpayPaymentTransaction();
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
            qpayPaymentTransaction.setConsumerAccount(account.get());
        }
        callLog.setUser(collectionTransaction.get().getLenderCall().getUser());
        QpayPaymentResponseDto qpayPaymentResponseDto = qpayPaymentService.payment(
                getPaymentPayload(collectionRequestDto, collectionTransaction.get(), callLog, qpayPaymentTransaction),
                callLog);
        qpayPaymentTransaction.setGatewayCustomerID(qpayPaymentResponseDto.getGatewayResponse().getGatewayCustomerId());
        qpayPaymentTransaction.setAuthorizedPayment(qpayPaymentResponseDto.getGatewayResponse().getAuthorizedPayment());
        qpayPaymentTransaction.setCollectionTransaction(collectionTransaction.get());
        collectionTransaction.get().getQpayPaymentTransaction().add(qpayPaymentTransaction);
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

    private QpayPaymentRequestDto getPaymentPayload(QpayCollectionRequestDto collectionRequestDto, CollectionTransaction collectionTransaction, LenderCallLog callLog, QpayPaymentTransaction qpayPaymentTransaction) {
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
        qpayPaymentTransaction.setGateway(collectionRequestDto.getGateway());
        qpayPaymentTransaction.setTransactionId(transactionId);
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

    @Override
    public QpayCollectionResponseDto qpayCollectionStatus(Long billId, PaymentGatewayType gatewayType, LenderCallLog callLog, String otp) {
        log.info(CALLING_SERVICE);
        log.info("In collectTroughQpay");
        Optional<CollectionTransaction> collectionTransaction = collectionTransactionService.geById(billId);
        if (collectionTransaction.get().getTransactionState().equals(TransactionState.RECEIVED)) {
            log.info(PaymentErrorType.ENABLE_TO_GET_STATUS.getErrorMessage());
            throw new ServiceException(PaymentErrorType.ENABLE_TO_GET_STATUS);
        }
        QpayPaymentTransaction qpayPaymentTransaction = collectionTransaction.get().getQpayPaymentTransaction().get(collectionTransaction.get().getQpayPaymentTransaction().size() > 1 ? collectionTransaction.get().getQpayPaymentTransaction().size() - 1 : 0);
        QpayPaymentResponseDto capture;
        QpayPaymentResponseDto status;
        if (collectionTransaction.get().getBillStatus().equals(BillStatusType.PAID)) {
            return QpayCollectionResponseDto.builder()
                    .authorizedPayment(qpayPaymentTransaction.getAuthorizedPayment())
                    .gateway(gatewayType)
                    .billId(collectionTransaction.get().getId())
                    .billStatus(collectionTransaction.get().getBillStatus())
                    .message(PaymentErrorType.BILL_PAID.getErrorMessage())
                    .transactionId(collectionTransaction.get().getServiceTransactionId())
                    .status("0000")
                    .source("")
                    .furtherAction(Boolean.FALSE)
                    .redirectURL("")
                    .build();
        }
        if (gatewayType.equals(PaymentGatewayType.NIFT) && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
            capture = qpayPaymentService.capture(
                    QpayCaptureRequestDto.builder()
                            .metadata(MetadataRequestDto.builder()
                                    .gatewayCredentials(new HashMap<>())
                                    .gateway(gatewayType.getName())
                                    .niftTransaction(
                                            NiftTransactionRequestDto.builder()
                                                    .otp(otp)
                                                    .refTransactionId(qpayPaymentTransaction.getGatewayCustomerID())
                                                    .bankId(qpayPaymentTransaction.getConsumerAccount().getBank().getCode())
                                                    .transactionId(collectionTransaction.get().getServiceTransactionId())
                                                    .build()).build())
                            .transactionId(collectionTransaction.get().getServiceTransactionId())
                            .build(), callLog);
            if (capture.getSuccess().equals(Boolean.TRUE)) {
                qpayPaymentTransaction.setAuthorizedPayment(capture.getGatewayResponse().getAuthorizedPayment());
                if (capture.getGatewayResponse().getGatewayMessage().equals("Success") && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
                    collectionTransaction.get().setBillStatus(BillStatusType.PAID);
                    collectionTransaction.get().setTransactionState(TransactionState.COMPLETED);
                }
            }
            collectionTransactionService.save(collectionTransaction.get());
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
        if (collectionTransaction.get().getServiceTransactionId() != null) {
            String statusUrl = String.format("/%s?gateway=%s", collectionTransaction.get().getServiceTransactionId(), gatewayType.getName());
            status = qpayPaymentService.status(statusUrl, callLog);
//            if (status.getGatewayResponse().getPaymentStatus().equals("Complete") && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
//                collectionTransaction.get().setBillStatus(BillStatusType.PAID);
//                collectionTransaction.get().setTransactionState(TransactionState.COMPLETED);
//            } else {
//                collectionTransaction.get().setBillStatus(BillStatusType.UNPAID);
//                collectionTransaction.get().setTransactionState(TransactionState.FAILURE);
//            }
            if (status.getGatewayResponse().getGatewayMessage().equals("PAID") && collectionTransaction.get().getBillStatus().equals(BillStatusType.UNPAID)) {
                qpayPaymentTransaction.setAuthorizedPayment(status.getGatewayResponse().getAuthorizedPayment());
                collectionTransaction.get().setBillStatus(BillStatusType.PAID);
                collectionTransaction.get().setTransactionState(TransactionState.COMPLETED);
            }
            collectionTransactionService.save(collectionTransaction.get());
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
    public EPCollectionInquiryResponse billInquiry(EPCollectionInquiryRequest epCollectionInquiryRequest) {
        log.info("Inquiry method has been invoked in CollectionServiceImpl class...");

        if (StringUtils.isBlank(epCollectionInquiryRequest.getConsumerNumber())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "consumer number is missing.");
        }

        AbroadInquiryRequest abroadInquiryRequest = AbroadInquiryRequest.builder().consumerNumber(epCollectionInquiryRequest.getConsumerNumber()).build();
        AbroadInquiryResponse abroadInquiryResponse;
        try {
            abroadInquiryResponse = abroadBillInquiryCall(abroadInquiryRequest);
        } catch (Exception e) {
            log.error("Exception Occurred in Abroad Inquiry for consumer: {}", epCollectionInquiryRequest.getConsumerNumber());
//            updateEpCallLog(savedEpLoginCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null);
//            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), abroadBaseUrl + billInquiryUrl, new HttpEntity<>(abroadInquiryRequest), environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (!abroadInquiryResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

//            //  update ep call log
//            updateEpCallLog(
//                    savedEpLoginCallLog,
//                    CallStatusType.FAILURE,
//                    epLoginResponse.getResponseCode(),
//                    epLoginResponse.getResponseMessage(),
//                    epLoginResponse.toString());
//
//            //  update lender call log
//            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);

            return EPCollectionInquiryResponse
                    .builder()
                    .responseCode(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getCode())
                    .responseMessage(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getDescription())
                    .build();
        }

//        //  update ep call log
//        updateEpCallLog(
//                savedEpLoginCallLog,
//                CallStatusType.SUCCESS,
//                epLoginResponse.getResponseCode(),
//                epLoginResponse.getResponseMessage(),
//                epLoginResponse.toString());

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
                .build();
    }

    @Override
    public EPCollectionBillUpdateResponse billUpdate(EPCollectionBillUpdateRequest epCollectionBillUpdateRequest) {
        log.info("Update method has been invoked in CollectionServiceImpl class...");

        if (StringUtils.isBlank(epCollectionBillUpdateRequest.getConsumerNumber())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "consumer number is missing.");
        }

        AbroadBillUpdateRequest abroadBillUpdateRequest = AbroadBillUpdateRequest.builder().consumerNumber(epCollectionBillUpdateRequest.getConsumerNumber()).build();
        AbroadBillUpdateResponse abroadBillUpdateResponse;
        try {
            abroadBillUpdateResponse = abroadBillUpdateCall(abroadBillUpdateRequest);
        } catch (Exception e) {
            log.error("Exception Occurred in Abroad Bill Update for consumer: {}", epCollectionBillUpdateRequest.getConsumerNumber());
//            updateEpCallLog(savedEpLoginCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null);
//            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), abroadBaseUrl + billUpdateUrl, new HttpEntity<>(abroadBillUpdateRequest), environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (!abroadBillUpdateResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

//            //  update ep call log
//            updateEpCallLog(
//                    savedEpLoginCallLog,
//                    CallStatusType.FAILURE,
//                    epLoginResponse.getResponseCode(),
//                    epLoginResponse.getResponseMessage(),
//                    epLoginResponse.toString());
//
//            //  update lender call log
//            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);

            return EPCollectionBillUpdateResponse
                    .builder()
                    .responseCode(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getCode())
                    .responseMessage(AbroadResponseCode.ABROAD_INQUIRY_FAILED.getDescription())
                    .build();
        }

//        //  update ep call log
//        updateEpCallLog(
//                savedEpLoginCallLog,
//                CallStatusType.SUCCESS,
//                epLoginResponse.getResponseCode(),
//                epLoginResponse.getResponseMessage(),
//                epLoginResponse.toString());

        return EPCollectionBillUpdateResponse
                .builder()
                .responseCode(abroadBillUpdateResponse.getResponseCode())
                .identificationParameter(abroadBillUpdateResponse.getIdentificationParameter())
                .tranAuthId("TO BE ADDED")
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

}
