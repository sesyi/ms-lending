package com.qisstpay.lendingservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.commons.enums.SlackTagType;
import com.qisstpay.commons.error.errortype.CommunicationErrorType;
import com.qisstpay.commons.exception.CustomException;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.config.cache.CustomCache;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqAuthRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqConsumerReportRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqReportDataRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqAuthResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqResponseDto;
import com.qisstpay.lendingservice.entity.LenderCallsHistory;
import com.qisstpay.lendingservice.entity.TasdeeqCallsHistory;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.EndPointType;
import com.qisstpay.lendingservice.repository.TasdeeqCallRepository;
import com.qisstpay.lendingservice.service.LendingCallService;
import com.qisstpay.lendingservice.service.TasdeeqService;
import com.qisstpay.lendingservice.utils.ModelConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class TasdeeqServiceImpl implements TasdeeqService {

    @Autowired
    private TasdeeqCallRepository tasdeeqCallRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ModelConverter modelConverter;

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
    @CustomCache(expiration = "30 * 1000")
    public TasdeeqAuthResponseDto authentication(Long requestId) {
        log.info(CALLING_TASDEEQ_SERVICE);
        log.info("Authentication");
        if (environment.equals("dev") || environment.equals("local")) {
            return TasdeeqAuthResponseDto.builder().auth_token("kSuRgfFYV8482nOdAc2QYAQsCKodUY").build();
        }
        TasdeeqAuthRequestDto tasdeeqAuthRequestDto =
                TasdeeqAuthRequestDto.builder()
                        .password(password)
                        .userName(username).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TasdeeqAuthRequestDto> requestEntity = new HttpEntity<>(tasdeeqAuthRequestDto, headers);
        String requestUrl = String.format(REQUEST_URL, baseUrl, authUrl);
        TasdeeqCallsHistory tasdeeqCallsHistory;
        if (requestId.equals(0L)) {
            tasdeeqCallsHistory =
                    tasdeeqCallRepository.save(
                            TasdeeqCallsHistory.builder()
                                    .id(requestId)
                                    .request(Objects.requireNonNull(requestEntity.getBody()).toString())
                                    .endPoint(EndPointType.AUTH)
                                    .requestedAt(Timestamp.valueOf(LocalDateTime.now()))
                                    .build());
        } else {
            tasdeeqCallsHistory =
                    tasdeeqCallRepository.save(
                            TasdeeqCallsHistory.builder()
                                    .request(Objects.requireNonNull(requestEntity.getBody()).toString())
                                    .endPoint(EndPointType.AUTH)
                                    .build());
        }
        ResponseEntity<TasdeeqResponseDto> response;
        try {
            response = restTemplate.postForEntity(requestUrl, requestEntity, TasdeeqResponseDto.class);
        } catch (Exception ex) {
            log.error("{} Request : {}", ex.getMessage(), tasdeeqCallsHistory.getRequest());
            tasdeeqCallsHistory.setStatus(CallStatusType.FAILURE);
            tasdeeqCallRepository.save(tasdeeqCallsHistory);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), requestUrl, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (response.getBody().getMessageCode().equals("00170017")) {
            tasdeeqCallsHistory.setStatus(CallStatusType.SUCCESS);
            tasdeeqCallsHistory.setMessage(response.getBody().getMessage());
            tasdeeqCallsHistory.setMessageCode(response.getBody().getMessageCode());
            tasdeeqCallsHistory.setStatusCode(response.getBody().getStatusCode());
            tasdeeqCallRepository.save(tasdeeqCallsHistory);
            return modelConverter.convertTOTasdeeqAuthResponseDto(response.getBody().getData());
        }
        tasdeeqCallsHistory.setStatus(CallStatusType.EXCEPTION);
        tasdeeqCallsHistory.setMessage(response.getBody().getMessage());
        tasdeeqCallsHistory.setMessageCode(response.getBody().getMessageCode());
        tasdeeqCallsHistory.setStatusCode(response.getBody().getStatusCode());
        tasdeeqCallRepository.save(tasdeeqCallsHistory);
        throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG);
    }

    @Override
    public TasdeeqConsumerReportResponseDto getConsumerReport(TasdeeqReportDataRequestDto tasdeeqReportDataRequestDto, String authToken, Long lenderCallId) throws JsonProcessingException {
        log.info(CALLING_TASDEEQ_SERVICE);
        log.info("getConsumerReport tasdeeqConsumerReportRequestDto: {}", tasdeeqReportDataRequestDto);
        LenderCallsHistory lenderCallsHistory = lendingCallService.getLendingCall(lenderCallId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        TasdeeqConsumerReportRequestDto requestBody = TasdeeqConsumerReportRequestDto.builder().reportDataObj(tasdeeqReportDataRequestDto).build();
        HttpEntity<TasdeeqConsumerReportRequestDto> requestEntity = new HttpEntity<>(requestBody, headers);
        String requestUrl = String.format(REQUEST_URL, baseUrl, consumerReportUrl);
        TasdeeqCallsHistory tasdeeqCallsHistory =
                tasdeeqCallRepository.save(
                        TasdeeqCallsHistory.builder()
                                .request(Objects.requireNonNull(requestEntity.getBody()).toString())
                                .endPoint(EndPointType.CONSUMER_REPORT)
                                .build());
        ResponseEntity<TasdeeqResponseDto> response;
        try {
            response = restTemplate.postForEntity(requestUrl, requestEntity, TasdeeqResponseDto.class);
        } catch (Exception ex) {
            log.error("{} Request : {}", ex.getMessage(), tasdeeqCallsHistory.getRequest());
            tasdeeqCallsHistory.setStatus(CallStatusType.FAILURE);
            tasdeeqCallRepository.save(tasdeeqCallsHistory);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), requestUrl, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (Objects.requireNonNull(response.getBody()).getStatusCode().equals("111")) {
            tasdeeqCallsHistory.setStatus(CallStatusType.SUCCESS);
            tasdeeqCallsHistory.setMessage(response.getBody().getMessage());
            tasdeeqCallsHistory.setMessageCode(response.getBody().getMessageCode());
            tasdeeqCallsHistory.setStatusCode(response.getBody().getStatusCode());
            tasdeeqCallRepository.save(tasdeeqCallsHistory);
            lenderCallsHistory.setStatus(CallStatusType.SUCCESS);
            lenderCallsHistory.setTasdeeqCall(tasdeeqCallsHistory);
            lendingCallService.saveLenderCall(lenderCallsHistory);
            return modelConverter.convertTOTasdeeqConsumerReportResponseDto(response.getBody().getData());
        } else if (Objects.requireNonNull(response.getBody()).getStatusCode().equals("112") || Objects.requireNonNull(response.getBody()).getMessageCode().equals("113")) {
            tasdeeqCallsHistory.setStatus(CallStatusType.EXCEPTION);
            tasdeeqCallsHistory.setMessage(response.getBody().getMessage());
            tasdeeqCallsHistory.setMessageCode(response.getBody().getMessageCode());
            tasdeeqCallsHistory.setStatusCode(response.getBody().getStatusCode());
            tasdeeqCallRepository.save(tasdeeqCallsHistory);
            TasdeeqAuthResponseDto authResponseDto = authentication(0L);
            headers.setBearerAuth(authResponseDto.getAuth_token());
            try {
                response = restTemplate.postForEntity(requestUrl, requestEntity, TasdeeqResponseDto.class);
            } catch (Exception ex) {
                log.error("{} Request : {}", ex.getMessage(), tasdeeqCallsHistory.getRequest());
                tasdeeqCallsHistory.setStatus(CallStatusType.FAILURE);
                tasdeeqCallRepository.save(tasdeeqCallsHistory);
                throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, ex, HttpMethod.POST.toString(), requestUrl, requestEntity, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
            }
            if (Objects.requireNonNull(response.getBody()).getStatusCode().equals("111")) {
                tasdeeqCallsHistory.setStatus(CallStatusType.SUCCESS);
                tasdeeqCallsHistory.setMessage(response.getBody().getMessage());
                tasdeeqCallsHistory.setMessageCode(response.getBody().getMessageCode());
                tasdeeqCallsHistory.setStatusCode(response.getBody().getStatusCode());
                tasdeeqCallRepository.save(tasdeeqCallsHistory);
                lenderCallsHistory.setStatus(CallStatusType.SUCCESS);
                lenderCallsHistory.setTasdeeqCall(tasdeeqCallsHistory);
                lendingCallService.saveLenderCall(lenderCallsHistory);
                return modelConverter.convertTOTasdeeqConsumerReportResponseDto(response.getBody().getData());

            }
        }
        tasdeeqCallsHistory.setStatus(CallStatusType.EXCEPTION);
        tasdeeqCallsHistory.setMessage(response.getBody().getMessage());
        tasdeeqCallsHistory.setMessageCode(response.getBody().getMessageCode());
        tasdeeqCallsHistory.setStatusCode(response.getBody().getStatusCode());
        tasdeeqCallRepository.save(tasdeeqCallsHistory);
        lenderCallsHistory.setStatus(CallStatusType.EXCEPTION);
        lenderCallsHistory.setTasdeeqCall(tasdeeqCallsHistory);
        lendingCallService.saveLenderCall(lenderCallsHistory);
        throw new CustomException(response.getBody().getMessageCode(), response.getBody().getMessage());
    }

    @Override
    public Long getLastAuthTokenId() {
        return tasdeeqCallRepository.findLastTokenId();
    }
}
