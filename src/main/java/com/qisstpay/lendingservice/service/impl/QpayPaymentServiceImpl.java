package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.enums.SlackTagType;
import com.qisstpay.commons.error.errortype.CommunicationErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.qpay.request.QpayCaptureRequestDto;
import com.qisstpay.lendingservice.dto.qpay.request.QpayPaymentRequestDto;
import com.qisstpay.lendingservice.dto.qpay.response.QpayPaymentResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.QPayPaymentCallLog;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.EndPointType;
import com.qisstpay.lendingservice.repository.QpayPaymentCallRepository;
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
    public QpayPaymentResponseDto payment(QpayPaymentRequestDto paymentRequestDto, LenderCallLog callLog) {
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
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), paymentURL, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        } finally {
            qpayPaymentCallRepository.save(qPayPaymentCallLog);
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
        log.info("In method status");
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
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), captureURL, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        } finally {
            qpayPaymentCallRepository.save(qPayPaymentCallLog);
        }
    }
}
