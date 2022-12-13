package com.qisstpay.lendingservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.commons.enums.SlackTagType;
import com.qisstpay.commons.error.errortype.CommunicationErrorType;
import com.qisstpay.commons.exception.CustomException;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.config.cache.CacheHelper;
import com.qisstpay.lendingservice.dto.communication.PhoneNumberResponseDto;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPLoginRequestDto;
import com.qisstpay.lendingservice.dto.easypaisa.request.EPRequestDto;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPInquiryResponseDto;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPLoginResponseDto;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPTransferResposneDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTokenResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTransactionStatusResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.SubmitTransactionResponseDto;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.CreditScoreResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqReportDataRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqAuthResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import com.qisstpay.lendingservice.encryption.EncryptionUtil;
import com.qisstpay.lendingservice.entity.*;
import com.qisstpay.lendingservice.enums.*;
import com.qisstpay.lendingservice.repository.*;
import com.qisstpay.lendingservice.service.*;
import com.qisstpay.lendingservice.utils.ModelConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RefreshScope
public class LendingServiceImpl implements LendingService {

    @Value("${ep.endpoints.login}")
    private String epLoginUrl;

    @Value("${ep.endpoints.inquiry}")
    private String epInquiryUrl;

    @Value("${ep.endpoints.transfer}")
    private String epTransferUrl;

    @Value("${ep.config.msisdn}")
    private String msisdn;

    @Value("${ep.config.pin}")
    private String pin;

    @Value("${environment}")
    private String environment;

    @Value("${message.slack.channel.third-party-errors}")
    private String thirdPartyErrorsSlackChannel;

    private static final String SUCCESS_STATUS_CODE = "0";

    private final String xChanelHeaderKey        = "X-Channel";
    private final String xChanelHeaderVal        = "subgateway";
    private final String xClientIdHeaderKey      = "X-IBM-Client-Id";
    private final String xClientIdHeaderVal      = "0d9fe5ca-8147-4b05-a9af-c7ef2e0df3af";
    private final String xClientSecretHeaderKey  = "X-IBM-Client-Secret";
    private final String xClientSecretHeaderVal  = "I4lR4yW0uP4yW3eQ7rR4vL0bK0pX6mV5cS7cN4iL7rC6pG2cA1";
    private final String xHashValueKey           = "X-Hash-Value";
    private final String CALLING_LENDING_SERVICE = "Calling lending Service";

    @Autowired
    CacheHelper cacheHelper;

    @Autowired
    private LendingTransactionRepository lendingTransactionRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private TasdeeqService tasdeeqService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private ConsumerCreditScoreService consumerCreditScoreService;

    @Autowired
    private LenderCallRepository lenderCallRepository;

    @Autowired
    private HMBCallLogRepository hmbCallLogRepository;

    @Autowired
    private ModelConverter modelConverter;

    @Autowired
    HMBPaymentServiceImpl hmbPaymentService;

    @Autowired
    private EPCallLogRepository epCallLogRepository;

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private LendingCallService lendingCallService;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private HMBBankRepository hmbBankRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public TransferResponseDto transfer(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog) throws JsonProcessingException {
        log.info("In LendingServiceImpl class...");

        if (StringUtils.isBlank(transferRequestDto.getPhoneNumber())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "phone number is missing.");
        }

        // Consumer sign-up, if not already
        Consumer savedConsumer = null;
        Consumer consumer = null;
        Optional<Consumer> existingConsumer = consumerRepository.findByPhoneNumber(transferRequestDto.getPhoneNumber());
        if (!existingConsumer.isPresent()) {
            Consumer newConsumer = new Consumer();
            newConsumer.setPhoneNumber(transferRequestDto.getPhoneNumber());
            savedConsumer = consumerRepository.saveAndFlush(newConsumer);
            consumer = savedConsumer;
        } else {
            consumer = existingConsumer.get();
        }

        if (transferRequestDto.getType() ==null || transferRequestDto.getType().equals(TransferType.EASYPAISA)) {
            return transferThroughEP(transferRequestDto, lenderCallLog, consumer);
        } else if (transferRequestDto.getType().equals(TransferType.HMB)) {
            return transferThroughHMB(transferRequestDto, lenderCallLog, consumer);

        }

        return null;
    }

    private TransferResponseDto transferThroughHMB(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog, Consumer consumer) {
        if (StringUtils.isBlank(transferRequestDto.getAccountNo())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "Account No is missing");
        }
        if (transferRequestDto.getBankCode() == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "Bank Code is missing");
        }

        LendingTransaction lendingTransaction = new LendingTransaction();
        lendingTransaction.setAmount(transferRequestDto.getAmount());
        lendingTransaction.setAccountNo(transferRequestDto.getAccountNo());
        lendingTransaction.setIdentityNumber(transferRequestDto.getIdentityNumber());
        lendingTransaction.setConsumer(consumer);

        HMBCallLog hmbCallLog = HMBCallLog.builder().build();

        hmbCallLog = hmbCallLogRepository.save(hmbCallLog);

        String transactionNo = environment.charAt(0) + "-"+ lenderCallLog.getUser().getId() + "-" + consumer.getId() + "-" + lenderCallLog.getId();
        String stan = environment.charAt(0) + "-" + hmbCallLog.toString();


        GetTokenResponseDto getTokenResponseDto = hmbPaymentService.getToken();

        if(getTokenResponseDto == null || getTokenResponseDto.getToken() == null){
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Something Went Wrong");
        }

        SubmitTransactionResponseDto submitTransactionResponseDto = null;

        Bank bank = bankRepository.findByCode(transferRequestDto.getBankCode()).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST.toString(), "Bank Code is incorrect")
        );

        HMBBank hmbBank = hmbBankRepository.getById(bank.getId());

        String bankCode = hmbBank.getCode();

        if(!environment.equals("prod")){ //in case of uat of hmb
            bankCode = "MDL";
        }

        try {
            submitTransactionResponseDto = hmbPaymentService.submitIBFTTransaction(getTokenResponseDto.getToken(), modelConverter.convertToSubmitTransactionRequestDtoIBFT(bankCode, transferRequestDto.getAccountNo(), transactionNo, stan, transferRequestDto.getAmount()));
        } catch (Exception e) {
            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.TRANSFER_FAILED.getDescription(), lenderCallLog);
            return TransferResponseDto
                    .builder()
                    .qpResponseCode(QPResponseCode.TRANSFER_FAILED.getCode())
                    .result(QPResponseCode.TRANSFER_FAILED.getDescription())
                    .build();
        }

        updateLenderCallLog(CallStatusType.SUCCESS, QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(), lenderCallLog);
        lendingTransaction.setLenderCall(lenderCallLog);
        lendingTransaction.setServiceType(ServiceType.HMB);
        lendingTransaction.setServiceTransactionId(transactionNo);
        lendingTransaction.setTransactionState(TransactionState.IN_PROGRESS);
        if(!submitTransactionResponseDto.getResponseCode().equals("00")){
            return TransferResponseDto
                    .builder()
                    .qpResponseCode(QPResponseCode.TRANSFER_FAILED.getCode())
                    .result(QPResponseCode.TRANSFER_FAILED.getDescription())
                    .build();
        }

        lendingTransaction = lendingTransactionRepository.save(lendingTransaction);

        return TransferResponseDto
                .builder()
                .qpResponseCode(QPResponseCode.SUCCESSFUL_EXECUTION.getCode())
                .result(QPResponseCode.SUCCESSFUL_EXECUTION.getDescription())
                .transactionId(lendingTransaction.getId().toString())
                .build();
    }

    private TransferResponseDto transferThroughEP(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog, Consumer consumer) throws JsonProcessingException {

        //  persist lending transaction
        LendingTransaction lendingTransaction = new LendingTransaction();
        lendingTransaction.setAmount(transferRequestDto.getAmount());
        lendingTransaction.setIdentityNumber(transferRequestDto.getIdentityNumber());
        lendingTransaction.setConsumer(consumer);
        lendingTransaction.setUserName(transferRequestDto.getUserName());
        lendingTransaction.setTransactionState(TransactionState.RECEIVED);
        lendingTransaction.setLenderCall(lenderCallLog);
        lendingTransaction.setServiceType(ServiceType.EP);
        LendingTransaction savedLendingTransaction = lendingTransactionRepository.saveAndFlush(lendingTransaction);

        /**
         *  ep login call
         */
        EPLoginRequestDto epLoginRequestDto = new EPLoginRequestDto();
        epLoginRequestDto.setLoginPayload(encryptionUtil.getEncryptedPayload(msisdn + ":" + pin));

        // add ep call logs
        EPCallLog savedEpLoginCallLog = addEPCalLog(
                EndPointType.LOGIN,
                epLoginRequestDto.toString(),
                savedLendingTransaction);

        EPLoginResponseDto epLoginResponse;
        try {
            epLoginResponse = epLogin(epLoginRequestDto);
        } catch (Exception e) {
            log.error("Exception Occurred in EP Login for consumer: {}", transferRequestDto.getPhoneNumber());
            updateEpCallLog(savedEpLoginCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null);
            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), epLoginUrl, epLoginRequestDto, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (!epLoginResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

            //  update ep call log
            updateEpCallLog(
                    savedEpLoginCallLog,
                    CallStatusType.FAILURE,
                    epLoginResponse.getResponseCode(),
                    epLoginResponse.getResponseMessage(),
                    epLoginResponse.toString());

            //  update lender call log
            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.EP_LOGIN_FAILED.getDescription(), lenderCallLog);

            return TransferResponseDto
                    .builder()
                    .qpResponseCode(QPResponseCode.EP_LOGIN_FAILED.getCode())
                    .result(QPResponseCode.EP_LOGIN_FAILED.getDescription())
//                    .epResult(epLoginResponse)
                    .build();
        }

        //  update ep call log
        updateEpCallLog(
                savedEpLoginCallLog,
                CallStatusType.SUCCESS,
                epLoginResponse.getResponseCode(),
                epLoginResponse.getResponseMessage(),
                epLoginResponse.toString());


        /**
         *  ep inquiry call
         */
        final String xHashValueVal = encryptionUtil.getEncryptedPayload(msisdn + "~" + epLoginResponse.getTimestamp() + "~" + pin);
        EPRequestDto epRequestDto = new EPRequestDto();
        epRequestDto.setAmount(transferRequestDto.getAmount());
        epRequestDto.setSubscriberMSISDN(msisdn);
        epRequestDto.setReceiverMSISDN(transferRequestDto.getPhoneNumber());

        // add ep call logs
        EPCallLog savedEpInquiryCallLog = addEPCalLog(
                EndPointType.INQUIRY,
                epRequestDto.toString(),
                savedLendingTransaction);

        // update lending transaction status
        savedLendingTransaction.setTransactionState(TransactionState.IN_PROGRESS);
        lendingTransactionRepository.saveAndFlush(savedLendingTransaction);

//        new ObjectMapper().writeValueAsString(epInquiryResponse);
        EPInquiryResponseDto epInquiryResponse;
        try {
            epInquiryResponse = epInquiry(epRequestDto, xHashValueVal);
        } catch (Exception e) {
            log.error("Exception Occurred in EP Inquiry for consumer: {}", transferRequestDto.getPhoneNumber());
            updateEpCallLog(savedEpInquiryCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null);
            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.EP_INQUIRY_FAILED.getDescription(), lenderCallLog);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), epInquiryUrl, epRequestDto, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (!epInquiryResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

            //  update ep call log
            updateEpCallLog(
                    savedEpInquiryCallLog,
                    CallStatusType.FAILURE,
                    epInquiryResponse.getResponseCode(),
                    epInquiryResponse.getResponseMessage(),
                    epInquiryResponse.toString());

            //  update lender call log
            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.EP_INQUIRY_FAILED.getDescription(), lenderCallLog);

            return TransferResponseDto
                    .builder()
                    .qpResponseCode(QPResponseCode.EP_INQUIRY_FAILED.getCode())
                    .result(QPResponseCode.EP_INQUIRY_FAILED.getDescription())
//                    .epResult(epInquiryResponse)
                    .build();
        }

        //  update ep call log
        updateEpCallLog(
                savedEpInquiryCallLog,
                CallStatusType.SUCCESS,
                epInquiryResponse.getResponseCode(),
                epInquiryResponse.getResponseMessage(),
                epInquiryResponse.toString());

        /**
         * ep transfer call
         */
        // add ep call logs
        EPCallLog savedEpTransferCallLog = addEPCalLog(
                EndPointType.TRANSFER,
                epRequestDto.toString(),
                savedLendingTransaction);

        EPTransferResposneDto epTransferResponse;
        try {
            epTransferResponse = epTransfer(epRequestDto, xHashValueVal);
        } catch (Exception e) {
            log.error("Exception Occurred in EP Transfer for consumer: {}", transferRequestDto.getPhoneNumber());
            updateEpCallLog(savedEpTransferCallLog, CallStatusType.EXCEPTION, null, e.getMessage(), null);
            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.TRANSFER_FAILED.getDescription(), lenderCallLog);
            throw new ServiceException(CommunicationErrorType.SOMETHING_WENT_WRONG, e, HttpMethod.POST.toString(), epTransferUrl, epRequestDto, environment, SlackTagType.JAVA_PRODUCT, thirdPartyErrorsSlackChannel);
        }
        if (epTransferResponse.getResponseCode().equals(SUCCESS_STATUS_CODE)) {

            //  update ep call log
            updateEpCallLog(
                    savedEpTransferCallLog,
                    CallStatusType.SUCCESS,
                    epTransferResponse.getResponseCode(),
                    epTransferResponse.getResponseMessage(),
                    epTransferResponse.toString());

            //  update lender call log
            updateLenderCallLog(CallStatusType.SUCCESS, QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(), lenderCallLog);

            // update lending transaction
            savedLendingTransaction.setServiceTransactionId(epTransferResponse.getTransactionReference());
            savedLendingTransaction.setTransactionState(TransactionState.COMPLETED);
            LendingTransaction finalSavedLendingTransaction = lendingTransactionRepository.saveAndFlush(savedLendingTransaction);

            return TransferResponseDto
                    .builder()
                    .qpResponseCode(QPResponseCode.SUCCESSFUL_EXECUTION.getCode())
                    .result(QPResponseCode.SUCCESSFUL_EXECUTION.getDescription())
//                    .epResult(epTransferResponse)
                    .transactionId(finalSavedLendingTransaction.getId().toString())
                    .build();
        } else {

            //  update ep call log
            updateEpCallLog(
                    savedEpTransferCallLog,
                    CallStatusType.FAILURE,
                    epTransferResponse.getResponseCode(),
                    epTransferResponse.getResponseMessage(),
                    epTransferResponse.toString());

            //  update lender call log
            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.TRANSFER_FAILED.getDescription(), lenderCallLog);

            return TransferResponseDto
                    .builder()
                    .qpResponseCode(QPResponseCode.TRANSFER_FAILED.getCode())
                    .result(QPResponseCode.TRANSFER_FAILED.getDescription())
//                    .epResult(epTransferResponse)
                    .build();
        }
    }

    private void updateLenderCallLog(CallStatusType status, String description, LenderCallLog lenderCallLog) {
        lenderCallLog.setStatus(status);
        lenderCallLog.setError(description);
        lenderCallRepository.saveAndFlush(lenderCallLog);
    }

    private EPCallLog addEPCalLog(EndPointType type, String request, LendingTransaction lendingTransaction) {
        EPCallLog epCallLog = new EPCallLog();
        epCallLog.setEndPoint(type);
        epCallLog.setRequest(request);
        epCallLog.setLendingTransaction(lendingTransaction);
        return epCallLogRepository.save(epCallLog);
    }

    private EPCallLog updateEpCallLog(EPCallLog savedEpCallLogs, CallStatusType status, String responseCode, String message, String response) {
        savedEpCallLogs.setStatus(status);
        savedEpCallLogs.setStatusCode(responseCode);
        savedEpCallLogs.setMessage(message);
        savedEpCallLogs.setResponse(response);
        return epCallLogRepository.save(savedEpCallLogs);
    }

    @Override
    public TransactionStateResponse checkStatus(String transactionId, LenderCallLog lenderCallLog) {

        LendingTransaction lendingTransaction = lendingTransactionRepository.findById(Long.valueOf(transactionId)).orElse(null);

        if (lendingTransaction != null) {
            ServiceType serviceType = lendingTransaction.getLenderCall().getServiceType();
            if(serviceType.equals(ServiceType.EP)){
                return checkEPStatus(lendingTransaction, lenderCallLog);
            }else if(serviceType.equals(ServiceType.HMB)){
                return checkHMBStatus(lendingTransaction, lenderCallLog);
            }
        }

        //  update lender call log
        updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.TRXN_FETCH_FAILED.getDescription(), lenderCallLog);
        throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "transaction not found.");
    }

    private TransactionStateResponse checkEPStatus(LendingTransaction lendingTransaction, LenderCallLog lenderCallLog) {

        TransactionStateResponse transactionStateResponse = TransactionStateResponse
                .builder()
                .state(lendingTransaction.getLenderCall().getStatus().toString())
                .description(lendingTransaction.getLenderCall().getError())
                .amount(lendingTransaction.getAmount())
                .identityNumber(lendingTransaction.getIdentityNumber())
                .phoneNumber(lendingTransaction.getConsumer().getPhoneNumber())
                .transactionId(lendingTransaction.getId().toString())
                .userName(lendingTransaction.getUserName())
                .build();
        //  update lender call log
        updateLenderCallLog(CallStatusType.SUCCESS, QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(), lenderCallLog);

        return transactionStateResponse;
    }

    private TransactionStateResponse checkHMBStatus(LendingTransaction lendingTransaction, LenderCallLog lenderCallLog) {

        HMBCallLog hmbCallLog = HMBCallLog.builder().build();
        hmbCallLog = hmbCallLogRepository.save(hmbCallLog);


        String stan = environment.charAt(0) + "-" + hmbCallLog.toString();

        String transactionNo = lendingTransaction.getServiceTransactionId();

        GetTokenResponseDto getTokenResponseDto = hmbPaymentService.getToken();

        GetTransactionStatusResponseDto getTransactionStatusResponseDto = null;
        try {
            getTransactionStatusResponseDto = hmbPaymentService.getStatus(getTokenResponseDto.getToken(), modelConverter.convertToGetTransactionStatusRequestDto(stan, transactionNo));
        } catch (Exception e) {
            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.TRXN_FETCH_FAILED.getDescription(), lenderCallLog);
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "transaction status request failed");
        }

        hmbCallLog = hmbCallLogRepository.save(hmbCallLog);

        updateLenderCallLog(CallStatusType.SUCCESS, QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(), lenderCallLog);

        return TransactionStateResponse
                .builder()
                .state(getTransactionStatusResponseDto.getResponseCode())
                .description(getTransactionStatusResponseDto.getResponseDescription())
                .amount(lendingTransaction.getAmount())
                .identityNumber(lendingTransaction.getIdentityNumber())
                .phoneNumber(lendingTransaction.getConsumer().getPhoneNumber())
                .accountNumber(lendingTransaction.getAccountNo())
                .transactionId(lendingTransaction.getId().toString())
                .userName(lendingTransaction.getUserName())
                .build();
    }

    @Override
    public CreditScoreResponseDto checkCreditScore(CreditScoreRequestDto creditScoreRequestDto, LenderCallLog lenderCallLog) throws JsonProcessingException {
        log.info(CALLING_LENDING_SERVICE);
        log.info("checkCreditScore -> CreditScoreRequestDto: {}", creditScoreRequestDto);
        if (StringUtils.isNotBlank(creditScoreRequestDto.getPhoneNumber())) {
            if (creditScoreRequestDto.getPhoneNumber().charAt(0) == '0') {
                creditScoreRequestDto.setPhoneNumber("92" + creditScoreRequestDto.getPhoneNumber().substring(1));
            }
            PhoneNumberResponseDto phoneNumberResponseDto = null;
            try {
                phoneNumberResponseDto = communicationService.phoneFormat(creditScoreRequestDto.getPhoneNumber());
            } catch (Exception ex) {
                log.error(ex.getMessage());
                lenderCallLog.setStatus(CallStatusType.FAILURE);
                lenderCallLog.setError(ex.toString());
                throw new ServiceException(CommunicationErrorType.COUNTRY_NOT_SUPPORTED);
            } finally {
                lenderCallRepository.save(lenderCallLog);
            }
            creditScoreRequestDto.setPhoneNumber(phoneNumberResponseDto.getBody().getCountryCode() + phoneNumberResponseDto.getBody().getNationalNumber());
        }
        TasdeeqReportDataRequestDto consumerReportRequestDto = createTasdeeqReportDataRequestDto(creditScoreRequestDto);
        Long authId = tasdeeqService.getLastAuthTokenId();
        TasdeeqAuthResponseDto authentication;
        try {
            authentication = tasdeeqService.authentication(authId != null ? authId : 0, Boolean.FALSE);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            lenderCallLog.setStatus(CallStatusType.FAILURE);
            lenderCallLog.setError(ex.toString());
            throw ex;
        } finally {
            lendingCallService.saveLenderCall(lenderCallLog);
        }
        TasdeeqConsumerReportResponseDto tasdeeqConsumerReportResponseDto = tasdeeqService.getConsumerReport(consumerReportRequestDto, lenderCallLog, authentication, authId);
        if (tasdeeqConsumerReportResponseDto.getCreditScoreData() != null) {
            Consumer consumer = consumerService.getOrCreateConsumerDetails(tasdeeqConsumerReportResponseDto, creditScoreRequestDto.getPhoneNumber());
            consumer.getConsumerCreditScoreData().add(consumerCreditScoreService.create(tasdeeqConsumerReportResponseDto.getCreditScoreData(), consumer, consumer.getCnic()));
            consumerService.save(consumer);
            return CreditScoreResponseDto.builder()
                    .score(tasdeeqConsumerReportResponseDto.getCreditScoreData().getScore())
                    .month(tasdeeqConsumerReportResponseDto.getCreditScoreData().getMonth())
                    .remarks(tasdeeqConsumerReportResponseDto.getCreditScoreData().getRemarks()).build();
        }
        return CreditScoreResponseDto.builder().score(527).month("SEP-2022").remarks("Excellent").build();
    }

    private TasdeeqReportDataRequestDto createTasdeeqReportDataRequestDto(CreditScoreRequestDto creditScoreRequestDto) {
        TasdeeqReportDataRequestDto consumerReportRequestDto = new TasdeeqReportDataRequestDto();
        consumerReportRequestDto.setCnic(creditScoreRequestDto.getCnic());
        consumerReportRequestDto.setLoanAmount(String.valueOf(creditScoreRequestDto.getLoanAmount()));
        return consumerReportRequestDto;
    }

    public EPLoginResponseDto epLogin(EPLoginRequestDto epLoginResponseDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(xChanelHeaderKey, xChanelHeaderVal);
        headers.add(xClientIdHeaderKey, xClientIdHeaderVal);
        headers.add(xClientSecretHeaderKey, xClientSecretHeaderVal);
        HttpEntity request = new HttpEntity(epLoginResponseDto, headers);

        ResponseEntity<EPLoginResponseDto> epLoginResponse =
                restTemplate.exchange(epLoginUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                });
        return epLoginResponse.getBody();
    }

    public EPInquiryResponseDto epInquiry(EPRequestDto epRequestDto, String xHashValueVal) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(xChanelHeaderKey, xChanelHeaderVal);
        headers.add(xClientIdHeaderKey, xClientIdHeaderVal);
        headers.add(xClientSecretHeaderKey, xClientSecretHeaderVal);
        headers.add(xHashValueKey, xHashValueVal);
        HttpEntity request = new HttpEntity(epRequestDto, headers);

        ResponseEntity<EPInquiryResponseDto> epInquiryResponse =
                restTemplate.exchange(epInquiryUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                });
        return epInquiryResponse.getBody();
    }

    public EPTransferResposneDto epTransfer(EPRequestDto epRequestDto, String xHashValueVal) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(xChanelHeaderKey, xChanelHeaderVal);
        headers.add(xClientIdHeaderKey, xClientIdHeaderVal);
        headers.add(xClientSecretHeaderKey, xClientSecretHeaderVal);
        headers.add(xHashValueKey, xHashValueVal);
        HttpEntity request = new HttpEntity(epRequestDto, headers);
//        try {
//            String json = new ObjectMapper().writeValueAsString(epRequestDto);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        ResponseEntity<EPTransferResposneDto> epTransferResponse =
                restTemplate.exchange(epTransferUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                });
        return epTransferResponse.getBody();
    }
}
