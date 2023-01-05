package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.enums.SlackTagType;
import com.qisstpay.commons.error.errortype.CommunicationErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.qpay.request.QpayCaptureRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.QpayPaymentRequestDto;
import com.qisstpay.lendingservice.dto.qpay.response.QpayPaymentResponseDto;
import com.qisstpay.lendingservice.entity.*;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.EndPointType;
import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import com.qisstpay.lendingservice.enums.TransactionState;
import com.qisstpay.lendingservice.error.errortype.PaymentErrorType;
import com.qisstpay.lendingservice.repository.QpayPaymentCallRepository;
import com.qisstpay.lendingservice.service.CollectionTransactionService;
import com.qisstpay.lendingservice.service.QpayPaymentService;
import com.qisstpay.lendingservice.utils.ModelConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RefreshScope
public class QpayPaymentServiceImpl implements QpayPaymentService {

    @Autowired
    private QpayPaymentCallRepository qpayPaymentCallRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ModelConverter modelConverter;

    @Autowired
    private CollectionTransactionService collectionTransactionService;

    @Value("${gateway.qpay-payment}")
    private String paymentURL;

    @Value("${gateway.qpay-payment-capture}")
    private String captureURL;

    @Value("${environment}")
    private String environment;

    @Value("${message.slack.channel.third-party-errors}")
    private String thirdPartyErrorsSlackChannel;

    private final String CALLING_SERVICE = "Calling QpayPayment Service";

    @Override
    public QpayPaymentTransaction payment(QpayPaymentRequestDto paymentRequestDto, LenderCallLog callLog, CollectionTransaction collectionTransaction, Optional<ConsumerAccount> account, PaymentGatewayType gateway) {
        log.info(CALLING_SERVICE);
        log.info("In method payment");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-api-key", "abc123!");
        HttpEntity<QpayPaymentRequestDto> requestEntity = new HttpEntity<>(paymentRequestDto, headers);
        QPayPaymentCallLog qPayPaymentCallLog =
                qpayPaymentCallRepository.save(
                        QPayPaymentCallLog.builder()
                                .request(Objects.requireNonNull(requestEntity.getBody()).toString())
                                .endPoint(EndPointType.QPAY_PAYMENT)
                                .lenderCall(callLog)
                                .build());
        ResponseEntity<QpayPaymentResponseDto> response;
        try {
            response = restTemplate.postForEntity(paymentURL, requestEntity, QpayPaymentResponseDto.class);
            if (response.getStatusCode().equals(HttpStatus.OK) || response.getStatusCodeValue() == 201) {
                qPayPaymentCallLog.setStatus(CallStatusType.SUCCESS);
                qPayPaymentCallLog.setMessage(Objects.requireNonNull(response.getBody()).getServiceMessage());
                qPayPaymentCallLog.setStatusCode(String.valueOf(response.getStatusCode()));
                collectionTransaction.setServiceTransactionId(response.getBody().getGatewayResponse().getGatewayResponseId());
                QpayPaymentTransaction qpayPaymentTransaction = QpayPaymentTransaction.builder()
                        .furtherAction(response.getBody().getFurtherAction())
                        .authorizedPayment(response.getBody().getGatewayResponse().getAuthorizedPayment())
                        .htmlSnippet(response.getBody().getHtmlSnippet())
                        .redirectURL(response.getBody().getRedirectURL())
                        .transactionId(paymentRequestDto.getTransactionId())
                        .refTransactionId(paymentRequestDto.getRefTransactionId())
                        .amount(paymentRequestDto.getAmount())
                        .gatewayCardSourceId(response.getBody().getGatewayResponse().getGatewayCardSourceId())
                        .gatewayClientSecret(response.getBody().getGatewayResponse().getGatewayClientSecret())
                        .gatewayCode(response.getBody().getGatewayResponse().getGatewayCode())
                        .gatewayMessage(response.getBody().getGatewayResponse().getGatewayMessage())
                        .gatewaySource(response.getBody().getGatewayResponse().getGatewaySource())
                        .gatewayStatus(response.getBody().getGatewayResponse().getGatewayStatus())
                        .gateway(gateway)
                        .build();
                account.ifPresent(qpayPaymentTransaction::setConsumerAccount);
                qpayPaymentTransaction.setCollectionTransaction(collectionTransaction);
                collectionTransaction.getQpayPaymentTransaction().add(qpayPaymentTransaction);
                collectionTransaction.setTransactionState(TransactionState.IN_PROGRESS);
                return qpayPaymentTransaction;
            }
            qPayPaymentCallLog.setStatus(CallStatusType.EXCEPTION);
            qPayPaymentCallLog.setMessage(Objects.requireNonNull(response.getBody()).getServiceMessage());
            qPayPaymentCallLog.setStatusCode(String.valueOf(response.getStatusCode()));
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG);
        } catch (Exception ex) {
            log.error("{} Request : {}", ex.getMessage(), qPayPaymentCallLog.getRequest());
            qPayPaymentCallLog.setMessage(ex.getMessage());
            qPayPaymentCallLog.setStatus(CallStatusType.FAILURE);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), paymentURL, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        } finally {
            qpayPaymentCallRepository.save(qPayPaymentCallLog);
            collectionTransactionService.save(collectionTransaction);
        }
    }

    @Override
    public QpayPaymentResponseDto status(String transactionIdAndGateway, LenderCallLog callLog) {
        log.info(CALLING_SERVICE);
        log.info("In method status");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-api-key", "abc123!");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        String requestUrl = paymentURL + transactionIdAndGateway;
        QPayPaymentCallLog qPayPaymentCallLog =
                qpayPaymentCallRepository.save(
                        QPayPaymentCallLog.builder()
                                .request(transactionIdAndGateway)
                                .endPoint(EndPointType.QPAY_STATUS)
                                .lenderCall(callLog)
                                .build());
        ResponseEntity<QpayPaymentResponseDto> response;
        try {
            response = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, QpayPaymentResponseDto.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                qPayPaymentCallLog.setStatus(CallStatusType.SUCCESS);
                qPayPaymentCallLog.setMessage(response.getBody().getServiceMessage());
                qPayPaymentCallLog.setStatusCode(String.valueOf(response.getStatusCode()));
                return response.getBody();
            }
            qPayPaymentCallLog.setStatus(CallStatusType.EXCEPTION);
            qPayPaymentCallLog.setMessage(response.getBody().getServiceMessage());
            qPayPaymentCallLog.setStatusCode(String.valueOf(response.getStatusCode()));
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG);
        } catch (Exception ex) {
            log.error("{} Request : {}", ex.getMessage(), qPayPaymentCallLog.getRequest());
            qPayPaymentCallLog.setMessage(ex.getMessage());
            qPayPaymentCallLog.setStatus(CallStatusType.FAILURE);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), requestUrl, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        } finally {
            qpayPaymentCallRepository.save(qPayPaymentCallLog);
        }
    }

    @Override
    public QpayPaymentResponseDto capture(QpayCaptureRequestDto captureRequestDto, LenderCallLog callLog) {
        log.info(CALLING_SERVICE);
        log.info("In method capture");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-api-key", "abc123!");
        HttpEntity<QpayCaptureRequestDto> requestEntity = new HttpEntity<>(captureRequestDto, headers);
        QPayPaymentCallLog qPayPaymentCallLog =
                qpayPaymentCallRepository.save(
                        QPayPaymentCallLog.builder()
                                .request(captureRequestDto.toString())
                                .endPoint(EndPointType.QPAY_CAPTURE)
                                .lenderCall(callLog)
                                .build());
        ResponseEntity<QpayPaymentResponseDto> response;
        try {
            response = restTemplate.postForEntity(captureURL, requestEntity, QpayPaymentResponseDto.class);
            if (response.getStatusCode().equals(HttpStatus.OK) || response.getStatusCodeValue() == 201) {
                qPayPaymentCallLog.setStatus(CallStatusType.SUCCESS);
                qPayPaymentCallLog.setMessage(Objects.requireNonNull(response.getBody()).getServiceMessage());
                qPayPaymentCallLog.setStatusCode(String.valueOf(response.getStatusCode()));
                return response.getBody();
            }
            qPayPaymentCallLog.setStatus(CallStatusType.EXCEPTION);
            qPayPaymentCallLog.setMessage(Objects.requireNonNull(response.getBody()).getServiceMessage());
            qPayPaymentCallLog.setStatusCode(String.valueOf(response.getStatusCode()));
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG);
        } catch (Exception ex) {
            log.error("{} Request : {}", ex.getMessage(), qPayPaymentCallLog.getRequest());
            if (ex.getMessage().contains("400 Bad Request")) {
                qPayPaymentCallLog.setStatus(CallStatusType.FAILURE);
                throw new ServiceException(PaymentErrorType.ENABLE_TO_CAPTURE, ex, HttpMethod.POST.toString(), captureURL, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
            }
            qPayPaymentCallLog.setMessage(ex.getMessage());
            qPayPaymentCallLog.setStatus(CallStatusType.FAILURE);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), captureURL, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        } finally {
            qpayPaymentCallRepository.save(qPayPaymentCallLog);
        }
    }
}
