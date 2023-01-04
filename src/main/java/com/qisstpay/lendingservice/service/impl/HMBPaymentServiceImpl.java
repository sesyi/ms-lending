package com.qisstpay.lendingservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qisstpay.commons.exception.CustomException;
import com.qisstpay.lendingservice.dto.hmb.request.GetTransactionStatusRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.HMBFetchAccountTitleRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitIBFTTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitIFTTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.response.*;
import com.qisstpay.lendingservice.dto.internal.TransactionStatusDto;
import com.qisstpay.lendingservice.dto.internal.request.FetchTitleRequestDto;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.FetchTitleResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.entity.*;
import com.qisstpay.lendingservice.enums.*;
import com.qisstpay.lendingservice.repository.*;
import com.qisstpay.lendingservice.service.HMBPaymentService;
import com.qisstpay.lendingservice.utils.CommonUtility;
import com.qisstpay.lendingservice.utils.ModelConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class HMBPaymentServiceImpl implements HMBPaymentService {

    @Value("${base-url.hmb-service}")
    private String hmbserviceBaseUrl;

    @Value("${credential.hmb-service.userid}")
    private String userId;

    @Value("${credential.hmb-service.password}")
    private String password;

    @Value("${environment}")
    private String environment;

    private String getTokenAPIBasePath = "/TransPaymentAPI/Transaction/GetToken";
    private String submitIFTTransactionBasePath = "/TransPaymentAPI/Transaction/TransSubmit";
    private String submitIBFTTransactionBasePath = "/TransPaymentAPI/Transaction/TransSubmit";
    private String getTransactionBasePath = "/TransPaymentAPI/Transaction/GetStatus";
    private String fetchAccountTitleBasePath = "/TransPaymentAPI/Transaction/TitleFetch";

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private HMBBankRepository hmbBankRepository;

    @Autowired
    private LendingTransactionRepository lendingTransactionRepository;

    @Autowired
    private HMBCallLogRepository hmbCallLogRepository;

    @Autowired
    private LenderCallRepository lenderCallRepository;

    @Autowired
    private ModelConverter modelConverter;

    //    @Qualifier("restTemplateWithoutSSL")
    @Autowired
    private RestTemplate restTemplateWithoutSSL;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public FetchTitleResponseDto fetchTitle(FetchTitleRequestDto fetchTitleRequestDto, LenderCallLog lenderCallLog) {

        GetTokenResponseDto getTokenResponseDto = callGetTokenApi();
        if(getTokenResponseDto == null || getTokenResponseDto.getToken() == null){
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Something Went Wrong");
        }

        String stan = generateStan(lenderCallLog.getId());
        HMBCallLog hmbCallLog = HMBCallLog.builder().build();
        hmbCallLog = hmbCallLogRepository.save(hmbCallLog);

        Bank bank = bankRepository.findByCode(fetchTitleRequestDto.getBankCode()).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST.toString(), "Bank Code is incorrect")
        );

        HMBBank hmbBank = hmbBankRepository.findByBankId(bank.getId());

        String bankCode = hmbBank.getCode();

        String productCode = "IBFT";
        if(bank.getCode().equals("MPBL")){
            productCode = "IFT";
        }

        try {
            HMBFetchAccountTitleResponseDto hmbFetchAccountTitleResponseDto = callFetchTitleApi(getTokenResponseDto.getToken(), modelConverter.convertToHMBFetchAccountTitleRequestDto(productCode, bankCode, fetchTitleRequestDto.getAccountNumber(), stan));
        } catch (Exception e) {
            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.TRANSFER_FAILED.getDescription(), lenderCallLog);
            e.printStackTrace();
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Something Went Wrong");
        }

        updateLenderCallLog(CallStatusType.SUCCESS, QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(), lenderCallLog);


        return FetchTitleResponseDto.builder()
                .bankCode(fetchTitleRequestDto.getBankCode())
                .accountNumber(fetchTitleRequestDto.getAccountNumber())
                .accountTitle("")
                .build();
    }

    @Override
    public TransferResponseDto transfer(TransferRequestDto transferRequestDto, LenderCallLog lenderCallLog, Consumer consumer) {

        TransferState transferState = TransferState.SOMETHING_WENT_WRONG;


        if (!StringUtils.isBlank(transferRequestDto.getAccountNo()) && StringUtils.isBlank(transferRequestDto.getAccountNumber())) {
            transferRequestDto.setAccountNumber(transferRequestDto.getAccountNo());
        }

        if (StringUtils.isBlank(transferRequestDto.getAccountNumber())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "Account No is missing");
        }
        if (transferRequestDto.getBankCode() == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "Bank Code is missing");
        }

        LendingTransaction lendingTransaction = new LendingTransaction();
        lendingTransaction.setAmount(transferRequestDto.getAmount());
        lendingTransaction.setAccountNumber(transferRequestDto.getAccountNumber());
        lendingTransaction.setServiceType(ServiceType.HMB);
        lendingTransaction.setConsumer(consumer);
        lendingTransaction.setTransactionStamp(CommonUtility.generateRandomTransactionStamp());
        lendingTransaction.setTransactionState(TransactionState.FAILURE);

        HMBCallLog hmbCallLog = HMBCallLog.builder().build();
        hmbCallLog = hmbCallLogRepository.save(hmbCallLog);

        String transactionNo = environment.toUpperCase().charAt(0) + "-"+ lenderCallLog.getUser().getId() + "-" + consumer.getId() + "-" + lenderCallLog.getId();
        lendingTransaction.setServiceTransactionId(transactionNo);

        lendingTransaction = lendingTransactionRepository.save(lendingTransaction);

        String stan = generateStan(lenderCallLog.getId());

        GetTokenResponseDto getTokenResponseDto = callGetTokenApi();

        if(getTokenResponseDto == null || getTokenResponseDto.getToken() == null){
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Something Went Wrong");
        }

        Bank bank = bankRepository.findByCode(transferRequestDto.getBankCode()).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST.toString(), "Bank Code is incorrect")
        );

        HMBBank hmbBank = hmbBankRepository.findByBankId(bank.getId());

        String bankCode = hmbBank.getCode();

        if(!environment.equals("prod")){ //in case of uat of hmb
            bankCode = "MDL";
        }

        try {
            if(bank.getCode().equals("MPBL")){ //ift for habib metro to habib metro
                SubmitIFTTransactionResponseDto submitIFTTransactionResponseDto = null;
                submitIFTTransactionResponseDto = callSubmitIFTTransactionApi(getTokenResponseDto.getToken(),modelConverter.convertToSubmitTransactionRequestDtoIFT(bankCode, transferRequestDto.getAccountNumber(), transactionNo, stan, transferRequestDto.getAmount()));
                transferState = getStatusFromStatusDescription(submitIFTTransactionResponseDto.getResponseCode(), submitIFTTransactionResponseDto.getResponseDescription());
            }else {
                SubmitIBFTTransactionResponseDto submitIBFTTransactionResponseDto = null;
                submitIBFTTransactionResponseDto = callSubmitIBFTTransactionApi(getTokenResponseDto.getToken(), modelConverter.convertToSubmitTransactionRequestDtoIBFT(bankCode, transferRequestDto.getAccountNumber(), transactionNo, stan, transferRequestDto.getAmount()));
                transferState = getStatusFromStatusDescription(submitIBFTTransactionResponseDto.getResponseCode(), submitIBFTTransactionResponseDto.getResponseDescription());
            }
            lendingTransaction.setTransactionState(TransactionState.SUCCESS);


        } catch (Exception e) {
            updateLenderCallLog(CallStatusType.EXCEPTION, QPResponseCode.TRANSFER_FAILED.getDescription(), lenderCallLog);

            return TransferResponseDto
                    .builder()
                  .code(transferState.getCode())
                    .state(transferState.getState())
                    .description(transferState.getDescription())
                    .build();
        }

        updateLenderCallLog(CallStatusType.SUCCESS, QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(), lenderCallLog);
        lendingTransaction.setLenderCall(lenderCallLog);

        lendingTransaction = lendingTransactionRepository.save(lendingTransaction);

        return TransferResponseDto
                .builder()
                .transactionId(lendingTransaction.getTransactionStamp())
                .code(transferState.getCode())
                .state(transferState.getState())
                .description(transferState.getDescription())

                .build();
    }

    @Override
    public TransactionStateResponse checkTransactionStatus(LendingTransaction lendingTransaction, LenderCallLog lenderCallLog) {

        HMBCallLog hmbCallLog = HMBCallLog.builder().build();
        hmbCallLog = hmbCallLogRepository.save(hmbCallLog);

        String stan = generateStan(lenderCallLog.getId());

        String transactionNo = lendingTransaction.getServiceTransactionId();

        GetTokenResponseDto getTokenResponseDto = callGetTokenApi();
        if(getTokenResponseDto == null || getTokenResponseDto.getToken() == null){
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Something Went Wrong");
        }

        GetTransactionStatusResponseDto getTransactionStatusResponseDto = null;
        try {
            getTransactionStatusResponseDto = callGetStatusApi(getTokenResponseDto.getToken(), modelConverter.convertToGetTransactionStatusRequestDto(stan, transactionNo));
        } catch (Exception e) {
            updateLenderCallLog(CallStatusType.FAILURE, QPResponseCode.TRXN_FETCH_FAILED.getDescription(), lenderCallLog);
            throw new CustomException(HttpStatus.BAD_REQUEST.toString(), "transaction status request failed");
        }

        hmbCallLog = hmbCallLogRepository.save(hmbCallLog);

        updateLenderCallLog(CallStatusType.SUCCESS, QPResponseCode.SUCCESSFUL_EXECUTION.getDescription(), lenderCallLog);

        TransferState transferState = getStatusFromStatusDescription(getTransactionStatusResponseDto.getResponseCode(), getTransactionStatusResponseDto.getResponseDescription());

        return TransactionStateResponse
                .builder()
                .code(transferState.getCode())
                .state(transferState.getState())
                .description(transferState.getDescription())
                .amount(lendingTransaction.getAmount())
                .phoneNumber(lendingTransaction.getConsumer().getPhoneNumber())
                .accountNumber(lendingTransaction.getAccountNumber())
                .transactionId(lendingTransaction.getTransactionStamp())
                .build();
    }

    private HttpURLConnection getConnection(URL url) throws IOException {
        if(environment.equals("prod")) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
            httpsConnection.setHostnameVerifier((hostname, session) -> true);
            return httpsConnection;
        }
        return  (HttpURLConnection) url.openConnection();
    }

    @Override
    public GetTokenResponseDto callGetTokenApi() {

        URL url = null;
        GetTokenResponseDto getTokenResponseDto = null;

        try {
            log.info("HMB Token URL : "+hmbserviceBaseUrl + getTokenAPIBasePath);

            url = new URL(hmbserviceBaseUrl + getTokenAPIBasePath);

            HttpURLConnection connection = getConnection(url);

            connection.setRequestProperty("insecure", "true");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("UserId", userId);
            connection.setRequestProperty("Password", password);


            // Send the request and get the response
            int responseCode = connection.getResponseCode();

            String responseBody = readInputStream(connection.getInputStream());
            log.info("HMB Token Response : "+ responseBody );

            if (responseCode == 200) {
                getTokenResponseDto = objectMapper.readValue(responseBody, GetTokenResponseDto.class);
            } else {
                System.out.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getTokenResponseDto;

//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(List.of(MediaType.ALL));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("UserId", userId);
//        headers.add("Password", password);
//        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//
//        GetTokenResponseDto getTokenResponseDto = null;
//
//        try{
//            log.info("HMB Token URL : "+hmbserviceBaseUrl + getTokenAPIBasePath);
//            String response = restTemplateWithoutSSL.exchange(hmbserviceBaseUrl + getTokenAPIBasePath, HttpMethod.GET, requestEntity, String.class).getBody();
//            log.info("HMB Token Response : "+ response );
//            getTokenResponseDto = objectMapper.readValue(response, GetTokenResponseDto.class);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return getTokenResponseDto;
    }

    @Override
    public HMBFetchAccountTitleResponseDto callFetchTitleApi(String authToken, HMBFetchAccountTitleRequestDto hmbFetchAccountTitleRequestDto) throws Exception {
        URL url = null;
        HMBFetchAccountTitleResponseDto hmbFetchAccountTitleResponseDto = null;

        try {
            log.info("HMB Fetch Title Request URL : "+hmbserviceBaseUrl + fetchAccountTitleBasePath);
            log.info("HMB Fetch Title Request Payload : " + objectMapper.writeValueAsString(hmbFetchAccountTitleRequestDto));

            url = new URL(hmbserviceBaseUrl + fetchAccountTitleBasePath);
            HttpURLConnection connection = getConnection(url);

            connection.setRequestProperty("insecure", "true");

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("UserId", userId);
            connection.setRequestProperty("Password", password);
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            connection.setRequestProperty("Content-Type", "application/json");


            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsString(hmbFetchAccountTitleRequestDto).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            String responseBody = readInputStream(connection.getInputStream());
            log.info("HMB Fetch Title Response : "+ responseBody );

            if (responseCode == 200) {
                hmbFetchAccountTitleResponseDto = objectMapper.readValue(responseBody, HMBFetchAccountTitleResponseDto.class);
            } else {
                System.out.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hmbFetchAccountTitleResponseDto;
    }

    @Override
    public SubmitIFTTransactionResponseDto callSubmitIFTTransactionApi(String authToken, SubmitIFTTransactionRequestDto submitIFTTransactionRequestDto) {

        URL url = null;

        SubmitIFTTransactionResponseDto submitIFTTransactionResponseDto = null;

        try {
            log.info("HMB Submit IFT Transaction URL : "+hmbserviceBaseUrl + submitIFTTransactionBasePath);
            log.info("HMB Submit IFT Transaction Request Payload : " + objectMapper.writeValueAsString(submitIFTTransactionRequestDto));

            url = new URL(hmbserviceBaseUrl + submitIFTTransactionBasePath);
            HttpURLConnection connection = getConnection(url);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("UserId", userId);
            connection.setRequestProperty("Password", password);
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            connection.setRequestProperty("insecure", "true");
            connection.setRequestProperty("Content-Type", "application/json");


            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsString(submitIFTTransactionRequestDto).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            String responseBody = readInputStream(connection.getInputStream());
            log.info("HMB Submit IFT Transaction Response : "+ responseBody );


            if (responseCode == 200) {
                // If successful, read the response body

                submitIFTTransactionResponseDto = objectMapper.readValue(responseBody, SubmitIFTTransactionResponseDto.class);

            } else {
                // If not successful, print the error message
                System.out.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return submitIFTTransactionResponseDto;

//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(List.of(MediaType.ALL));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("UserId", userId);
//        headers.add("Password", password);
//        headers.add("Authorization", "Bearer " + authToken);
//        HttpEntity<SubmitTransactionRequestDto> requestEntity = new HttpEntity<SubmitTransactionRequestDto>(submitTransactionRequestDto, headers);
//
//        SubmitTransactionResponseDto submitTransactionResponseDto = null;
//
//        try {
//            String response = restTemplateWithoutSSL.exchange(hmbserviceBaseUrl + submitIFTTransactionBasePath, HttpMethod.POST, requestEntity, String.class).getBody();
//            submitTransactionResponseDto = objectMapper.readValue(response, SubmitTransactionResponseDto.class);
//        } catch (Exception e) {
//
//        }
//        return submitTransactionResponseDto;
    }

    @Override
    public SubmitIBFTTransactionResponseDto callSubmitIBFTTransactionApi(String authToken, SubmitIBFTTransactionRequestDto IBFTSubmitTransactionRequestDto) throws Exception {

        URL url = null;

        SubmitIBFTTransactionResponseDto submitIBFTTransactionResponseDto = null;

        try {
            log.info("HMB Submit IBFT Transaction URL : "+hmbserviceBaseUrl + submitIBFTTransactionBasePath);
            log.info("HMB IBFT Transfer Request Payload : " + objectMapper.writeValueAsString(IBFTSubmitTransactionRequestDto));

            url = new URL(hmbserviceBaseUrl + submitIBFTTransactionBasePath);
            HttpURLConnection connection = getConnection(url);

//            log.info("Https Client: HttpsURLConnection");

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("UserId", userId);
            connection.setRequestProperty("Password", password);
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            connection.setRequestProperty("insecure", "true");
            connection.setRequestProperty("Content-Type", "application/json");

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsString(IBFTSubmitTransactionRequestDto).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            String responseBody = readInputStream(connection.getInputStream());
            log.info("HMB Submit IBFT Transaction Response : "+ responseBody );


            if (responseCode == 200) {
                // If successful, read the response body

                submitIBFTTransactionResponseDto = objectMapper.readValue(responseBody, SubmitIBFTTransactionResponseDto.class);

            } else {
                // If not successful, print the error message
                System.out.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return submitIBFTTransactionResponseDto;

//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(List.of(MediaType.ALL));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("UserId", userId);
//        headers.add("Password", password);
//        headers.add("Authorization", "Bearer " + authToken);
//        HttpEntity<SubmitTransactionRequestDto> requestEntity = new HttpEntity<>(submitTransactionRequestDto, headers);
//
//        SubmitTransactionResponseDto submitTransactionResponseDto = null;
//
//        try {
//            log.info("HMB IBFT Transfer URL : " + hmbserviceBaseUrl + submitIFTTransactionBasePath);
//            log.info("HMB IBFT Transfer Request Payload : " + objectMapper.writeValueAsString(submitTransactionRequestDto));
//            String response = restTemplateWithoutSSL.exchange(hmbserviceBaseUrl + submitIBFTTransactionBasePath, HttpMethod.POST, requestEntity, String.class).getBody();
//            log.info("HMB IBFT Response: " + response);
//            submitTransactionResponseDto = objectMapper.readValue(response, SubmitTransactionResponseDto.class);
//        } catch (Exception e) {
//            throw e;
//        }
//
//        return submitTransactionResponseDto;
    }

    @Override
    public GetTransactionStatusResponseDto callGetStatusApi(String authToken, GetTransactionStatusRequestDto getTransactionStatusRequestDto) throws Exception {

        URL url = null;
        GetTransactionStatusResponseDto getTransactionStatusResponseDto = null;

        try {
            log.info("HMB Transaction Status Request URL : "+hmbserviceBaseUrl + getTransactionBasePath);
            log.info("HMB Transaction Status Request Payload : " + objectMapper.writeValueAsString(getTransactionStatusRequestDto));

            url = new URL(hmbserviceBaseUrl + getTransactionBasePath);
            HttpURLConnection connection = getConnection(url);

            connection.setRequestProperty("insecure", "true");

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("UserId", userId);
            connection.setRequestProperty("Password", password);
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            connection.setRequestProperty("Content-Type", "application/json");


            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsString(getTransactionStatusRequestDto).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            String responseBody = readInputStream(connection.getInputStream());
            log.info("HMB Transaction Status Response : "+ responseBody );

            if (responseCode == 200) {
                getTransactionStatusResponseDto = objectMapper.readValue(responseBody, GetTransactionStatusResponseDto.class);
            } else {
                System.out.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getTransactionStatusResponseDto;
//
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(List.of(MediaType.ALL));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("UserId", userId);
//        headers.add("Password", password);
//        headers.add("Authorization", "Bearer " + authToken);
//        HttpEntity<GetTransactionStatusRequestDto> requestEntity = new HttpEntity<>(getTransactionStatusRequestDto, headers);
//
//        GetTransactionStatusResponseDto getTransactionStatusResponseDto = null;
//
//        try {
//            log.info("HMB Transaction Status URL : " + hmbserviceBaseUrl + getTransactionBasePath);
//            log.info("HMB Transaction Status Request Payload : " + objectMapper.writeValueAsString(getTransactionStatusRequestDto));
//
//            String response = restTemplateWithoutSSL.exchange(hmbserviceBaseUrl + getTransactionBasePath, HttpMethod.POST, requestEntity, String.class).getBody();
//            log.info("HMB Transaction Status Response: " + response);
//            getTransactionStatusResponseDto = objectMapper.readValue(response, GetTransactionStatusResponseDto.class);
//        } catch (Exception e) {
//            throw e;
//        }
//
//        return getTransactionStatusResponseDto;
    }

    public TransferState getStatusFromStatusDescription(String responseCode, String responseDescription){

        TransactionStatusDto transactionStatusDto = new TransactionStatusDto();

        responseDescription = responseDescription.toLowerCase();

        if(responseDescription.equals("Paid/Transferred".toLowerCase())){
            return TransferState.TRANSFER_SUCCESS;
        }
        if(responseDescription.contains("Insufficient funds".toLowerCase())){
            return TransferState.INSUFFICIENT_FUNDS;
        }
        if(responseDescription.contains("Transfer limit exceeded".toLowerCase())){
            return TransferState.TRANSFER_LIMIT_EXCEEDED;
        }
        if(responseDescription.contains("To be release".toLowerCase())){
            if(!environment.equals("prod")){
                return TransferState.TRANSFER_SUCCESS;
            }
            return TransferState.RELEASER_AUTHORIZATION_NEEDED;
        }
        if(responseDescription.contains("Currency mismatch for beneficiary".toLowerCase())){
            return TransferState.CURRENCY_MISMATCH;
        }
        if(responseDescription.contains("Customer account is not found".toLowerCase())){
            return TransferState.RECIPIENT_ACCOUNT_NOT_FOUND;
        }
        if(responseDescription.contains("Customer account is inactive".toLowerCase())){
            return TransferState.RECIPIENT_ACCOUNT_INACTIVE;
        }
        if(responseDescription.contains("Rejected by releaser".toLowerCase())){
            return TransferState.RELEASER_REJECTED;
        }
        if(responseDescription.contains("Core UnSuccess Failed".toLowerCase())){
            return TransferState.SOMETHING_WENT_WRONG;
        }
        if(responseDescription.contains("waiting for core response".toLowerCase())){
            return TransferState.GATEWAY_TRANSFER_PENDING;
        }
        if(responseDescription.contains("Waiting for backoffice process".toLowerCase())){
            return TransferState.GATEWAY_TRANSFER_PENDING;
        }

        return TransferState.SOMETHING_WENT_WRONG;
    }

    private void updateLenderCallLog(CallStatusType status, String description, LenderCallLog lenderCallLog) {
        lenderCallLog.setStatus(status);
        lenderCallLog.setError(description);
        lenderCallRepository.saveAndFlush(lenderCallLog);
    }

    // Helper method to read the response body from an InputStream
    private static String readInputStream (InputStream inputStream) throws IOException {
        // Read the input stream into a BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Read the first line of the response
        String line = reader.readLine();

        // Initialize a StringBuilder to hold the response body
        StringBuilder responseBody = new StringBuilder();

        // Keep reading lines from the response until there are no more
        while (line != null) {
            // Append the current line to the response body
            responseBody.append(line);

            // Read the next line
            line = reader.readLine();
        }

        // Return the response body as a String
        return responseBody.toString();
    }

    private String generateStan(Long lenderCallLogId){

        String stan = lenderCallLogId.toString();

        if(environment.equals("stag")){
            stan = "2"+stan;
        }
        else if(environment.equals("dev")){
            stan = "3"+stan;
        }

        if(lenderCallLogId<100000){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i =0; i<6 - stan.length();i++){
                stringBuilder.append("0");
            }
            stan = stringBuilder.append(lenderCallLogId).toString();
        }
        return stan;
    }



}
