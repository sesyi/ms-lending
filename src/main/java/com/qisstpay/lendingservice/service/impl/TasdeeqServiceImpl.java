package com.qisstpay.lendingservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.commons.enums.SlackTagType;
import com.qisstpay.commons.error.errortype.CommunicationErrorType;
import com.qisstpay.commons.exception.CustomException;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.config.cache.CacheHelper;
import com.qisstpay.lendingservice.config.cache.CustomCache;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqAuthRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqConsumerReportRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqReportDataRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqAuthResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallLog;
import com.qisstpay.lendingservice.entity.TasdeeqCallLog;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.EndPointType;
import com.qisstpay.lendingservice.repository.TasdeeqCallRepository;
import com.qisstpay.lendingservice.service.LendingCallService;
import com.qisstpay.lendingservice.service.TasdeeqService;
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
public class TasdeeqServiceImpl implements TasdeeqService {

    @Autowired
    private TasdeeqCallRepository tasdeeqCallRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ModelConverter modelConverter;

    @Autowired
    CacheHelper cacheHelper;

    @Autowired
    private LendingCallService lendingCallService;

    @Value("${endpoints.tasdeeq.auth}")
    private String authUrl;

    @Value("${endpoints.tasdeeq.consumer-report}")
    private String consumerReportUrl;

    @Value("${endpoints.tasdeeq.base}")
    private String baseUrl;

    @Value("${environment}")
    private String environment;

    @Value("${message.slack.channel.third-party-errors}")
    private String thirdPartyErrorsSlackChannel;

    @Value("${credential.tasdeeq.username}")
    private String username;

    @Value("${credential.tasdeeq.password}")
    private String password;

    private final String REQUEST_URL             = "%s%s";
    private final String CALLING_TASDEEQ_SERVICE = "Calling Tasdeeq Service";

    @Override
    @CustomCache(expiration = "@cacheProperties.getAuthToken()", cacheManager = "@redisCacheManager")
    public TasdeeqAuthResponseDto authentication(Long requestId, Boolean clearCache) {
        log.info(CALLING_TASDEEQ_SERVICE);
        log.info("authentication requestId: {}", requestId);
        if (!environment.equals("prod")) {
            return TasdeeqAuthResponseDto.builder().auth_token("testToken").build();
        }
        TasdeeqAuthRequestDto tasdeeqAuthRequestDto =
                TasdeeqAuthRequestDto.builder()
                        .password(password)
                        .userName(username).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TasdeeqAuthRequestDto> requestEntity = new HttpEntity<>(tasdeeqAuthRequestDto, headers);
        String requestUrl = String.format(REQUEST_URL, baseUrl, authUrl);
        TasdeeqCallLog tasdeeqCallLog =
                tasdeeqCallRepository.save(
                        TasdeeqCallLog.builder()
                                .request(Objects.requireNonNull(requestEntity.getBody()).toString())
                                .endPoint(EndPointType.AUTH)
                                .build());
        ResponseEntity<TasdeeqResponseDto> response;
        try {
            response = restTemplate.postForEntity(requestUrl, requestEntity, TasdeeqResponseDto.class);
            if (response.getBody().getMessageCode().equals("00170017")) {
                tasdeeqCallLog.setStatus(CallStatusType.SUCCESS);
                tasdeeqCallLog.setMessage(response.getBody().getMessage());
                tasdeeqCallLog.setMessageCode(response.getBody().getMessageCode());
                tasdeeqCallLog.setStatusCode(response.getBody().getStatusCode());
                if (clearCache.equals(Boolean.TRUE)) {
                    cacheHelper.removeAuthTokenAndIdFromCache(requestId);
                    cacheHelper.addAuthIdToCache(tasdeeqCallLog.getId());
                }
                return modelConverter.convertTOTasdeeqAuthResponseDto(response.getBody().getData());
            }
            tasdeeqCallLog.setStatus(CallStatusType.EXCEPTION);
            tasdeeqCallLog.setMessage(response.getBody().getMessage());
            tasdeeqCallLog.setMessageCode(response.getBody().getMessageCode());
            tasdeeqCallLog.setStatusCode(response.getBody().getStatusCode());
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG);
        } catch (Exception ex) {
            log.error("{} Request : {}", ex.getMessage(), tasdeeqCallLog.getRequest());
            tasdeeqCallLog.setMessage(ex.getMessage());
            tasdeeqCallLog.setStatus(CallStatusType.FAILURE);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), requestUrl, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        } finally {
            tasdeeqCallRepository.save(tasdeeqCallLog);
        }
    }

    @Override
    public TasdeeqConsumerReportResponseDto getConsumerReport(TasdeeqReportDataRequestDto tasdeeqReportDataRequestDto, LenderCallLog lenderCallLog, TasdeeqAuthResponseDto authentication, Long authTokenId) throws JsonProcessingException {
        log.info(CALLING_TASDEEQ_SERVICE);
        log.info("getConsumerReport tasdeeqConsumerReportRequestDto: {}", tasdeeqReportDataRequestDto);
        log.info("authentication: {}", authentication);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!environment.equals("prod")) {
            lenderCallLog.setStatus(CallStatusType.SUCCESS);
            lendingCallService.saveLenderCall(lenderCallLog);
            return TasdeeqConsumerReportResponseDto.builder().build();
        }
        headers.setBearerAuth(authentication.getAuth_token());
        TasdeeqConsumerReportRequestDto requestBody = TasdeeqConsumerReportRequestDto.builder().reportDataObj(tasdeeqReportDataRequestDto).build();
        HttpEntity<TasdeeqConsumerReportRequestDto> requestEntity = new HttpEntity<>(requestBody, headers);
        String requestUrl = String.format(REQUEST_URL, baseUrl, consumerReportUrl);
        TasdeeqCallLog tasdeeqCallLog =
                tasdeeqCallRepository.save(
                        TasdeeqCallLog.builder()
                                .request(Objects.requireNonNull(requestEntity.getBody()).toString())
                                .lenderCall(lenderCallLog)
                                .endPoint(EndPointType.CONSUMER_REPORT)
                                .build());
        ResponseEntity<TasdeeqResponseDto> response;
        try {
            response = restTemplate.postForEntity(requestUrl, requestEntity, TasdeeqResponseDto.class);
            if (Objects.requireNonNull(response.getBody()).getStatusCode().equals("112") || Objects.requireNonNull(response.getBody()).getMessageCode().equals("113")) {
                tasdeeqCallLog.setStatus(CallStatusType.EXCEPTION);
                tasdeeqCallLog.setMessage(response.getBody().getMessage());
                tasdeeqCallLog.setMessageCode(response.getBody().getMessageCode());
                tasdeeqCallLog.setStatusCode(response.getBody().getStatusCode());
                tasdeeqCallRepository.save(tasdeeqCallLog);
                tasdeeqCallLog =
                        tasdeeqCallRepository.save(
                                TasdeeqCallLog.builder()
                                        .request(Objects.requireNonNull(requestEntity.getBody()).toString())
                                        .lenderCall(lenderCallLog)
                                        .endPoint(EndPointType.CONSUMER_REPORT)
                                        .build());
                TasdeeqAuthResponseDto authResponseDto = authentication(authTokenId,Boolean.TRUE);
                headers.setBearerAuth(authResponseDto.getAuth_token());
                try {
                    response = restTemplate.postForEntity(requestUrl, requestEntity, TasdeeqResponseDto.class);
                } catch (Exception ex) {
                    log.error("{} Request : {}", ex.getMessage(), tasdeeqCallLog.getRequest());
                    tasdeeqCallLog.setStatus(CallStatusType.FAILURE);
                    lenderCallLog.setStatus(CallStatusType.FAILURE);
                    tasdeeqCallLog.setLenderCall(lenderCallLog);
                    tasdeeqCallLog.setMessage(ex.getMessage());
                    tasdeeqCallRepository.save(tasdeeqCallLog);
                    throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), requestUrl, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
                }
            }
            if (Objects.requireNonNull(response.getBody()).getStatusCode().equals("111")) {
                lenderCallLog.setStatus(CallStatusType.SUCCESS);
                tasdeeqCallLog.setStatus(CallStatusType.SUCCESS);
                tasdeeqCallLog.setMessage(response.getBody().getMessage());
                tasdeeqCallLog.setMessageCode(response.getBody().getMessageCode());
                tasdeeqCallLog.setStatusCode(response.getBody().getStatusCode());
                tasdeeqCallLog.setLenderCall(lenderCallLog);
                return modelConverter.convertTOTasdeeqConsumerReportResponseDto(response.getBody().getData());
            }
            lenderCallLog.setStatus(CallStatusType.EXCEPTION);
            tasdeeqCallLog.setStatus(CallStatusType.EXCEPTION);
            tasdeeqCallLog.setMessage(response.getBody().getMessage());
            tasdeeqCallLog.setMessageCode(response.getBody().getMessageCode());
            tasdeeqCallLog.setStatusCode(response.getBody().getStatusCode());
            tasdeeqCallLog.setLenderCall(lenderCallLog);
            throw new CustomException(response.getBody().getMessageCode(), response.getBody().getMessage());
        } catch (Exception ex) {
            if (ex.getMessage().contains("403")) {
                tasdeeqCallLog.setStatus(CallStatusType.FAILURE);
                tasdeeqCallLog.setMessage(ex.getMessage());
                tasdeeqCallRepository.save(tasdeeqCallLog);
                tasdeeqCallLog =
                        tasdeeqCallRepository.save(
                                TasdeeqCallLog.builder()
                                        .request(Objects.requireNonNull(requestEntity.getBody()).toString())
                                        .lenderCall(lenderCallLog)
                                        .endPoint(EndPointType.CONSUMER_REPORT)
                                        .build());
                TasdeeqAuthResponseDto authResponseDto = authentication(authTokenId,Boolean.TRUE);
                headers.setBearerAuth(authResponseDto.getAuth_token());
                try {
                    response = restTemplate.postForEntity(requestUrl, requestEntity, TasdeeqResponseDto.class);
                    if (Objects.requireNonNull(response.getBody()).getStatusCode().equals("111")) {
                        lenderCallLog.setStatus(CallStatusType.SUCCESS);
                        tasdeeqCallLog.setStatus(CallStatusType.SUCCESS);
                        tasdeeqCallLog.setMessage(response.getBody().getMessage());
                        tasdeeqCallLog.setMessageCode(response.getBody().getMessageCode());
                        tasdeeqCallLog.setStatusCode(response.getBody().getStatusCode());
                        tasdeeqCallLog.setLenderCall(lenderCallLog);
                        return modelConverter.convertTOTasdeeqConsumerReportResponseDto(response.getBody().getData());
                    }
                    lenderCallLog.setStatus(CallStatusType.EXCEPTION);
                    tasdeeqCallLog.setStatus(CallStatusType.EXCEPTION);
                    tasdeeqCallLog.setMessage(response.getBody().getMessage());
                    tasdeeqCallLog.setMessageCode(response.getBody().getMessageCode());
                    tasdeeqCallLog.setStatusCode(response.getBody().getStatusCode());
                    tasdeeqCallLog.setLenderCall(lenderCallLog);
                    if (Objects.requireNonNull(response.getBody()).getStatusCode().equals("112") || Objects.requireNonNull(response.getBody()).getMessageCode().equals("113")) {
                        throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG);
                    }
                    throw new CustomException(response.getBody().getMessageCode(), response.getBody().getMessage());
                } catch (Exception e) {
                }
            }
            log.error("{} Request : {}", ex.getMessage(), tasdeeqCallLog.getRequest());
            tasdeeqCallLog.setStatus(CallStatusType.FAILURE);
            tasdeeqCallLog.setMessage(ex.getMessage());
            lenderCallLog.setStatus(CallStatusType.FAILURE);
            tasdeeqCallLog.setLenderCall(lenderCallLog);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), requestUrl, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        } finally {
            tasdeeqCallRepository.save(tasdeeqCallLog);
        }
    }

    @Override
    @CustomCache(expiration = "@cacheProperties.getAuthToken()", cacheManager = "@redisCacheManager")
    public Long getLastAuthTokenId() {
        log.info(CALLING_TASDEEQ_SERVICE);
        log.info("getLastAuthTokenId");
        return tasdeeqCallRepository.findLastTokenId();
    }
}
