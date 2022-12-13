package com.qisstpay.lendingservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qisstpay.lendingservice.dto.hmb.request.GetTransactionStatusRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTokenResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTransactionStatusResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.SubmitTransactionResponseDto;
import com.qisstpay.lendingservice.repository.BankRepository;
import com.qisstpay.lendingservice.repository.HMBBankRepository;
import com.qisstpay.lendingservice.service.HMBPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@Slf4j
public class HMBPaymentServiceImpl implements HMBPaymentService {

    @Value("${base-url.hmb-service}")
    private String hmbserviceBaseUrl;

    @Value("${credential.hmb-service.userid}")
    private String userId;

    @Value("${credential.hmb-service.password}")
    private String password;

    private String getTokenAPIBasePath = "/TransPaymentAPI/Transaction/GetToken";
    private String submitIFTTransactionBasePath = "/TransPaymentAPI/Transaction/TransSubmit";
    private String submitIBFTTransactionBasePath = "/TransPaymentAPI/Transaction/TransSubmit";
    private String getTransactionBasePath = "/TransPaymentAPI/Transaction/GetStatus";

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private HMBBankRepository hmbBankRepository;

    //    @Qualifier("restTemplateWithoutSSL")
    @Autowired
    private RestTemplate restTemplateWithoutSSL;

    @Autowired
    private ObjectMapper objectMapper;

//    @Override
//    public GetTokenResponseDto getToken() {
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
//    }

    @Override
    public GetTokenResponseDto getToken() {

        URL url = null;
        GetTokenResponseDto getTokenResponseDto = null;

        try {
            log.info("HMB Token URL : "+hmbserviceBaseUrl + getTokenAPIBasePath);

            url = new URL(hmbserviceBaseUrl + getTokenAPIBasePath);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier((hostname, session) -> true);
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
    public SubmitTransactionResponseDto submitIFTTransaction(String authToken, SubmitTransactionRequestDto submitTransactionRequestDto) {

        URL url = null;

        SubmitTransactionResponseDto submitTransactionResponseDto = null;

        try {
            log.info("HMB Submit IFT Transaction URL : "+hmbserviceBaseUrl + submitIFTTransactionBasePath);
            log.info("HMB Submit IFT Transaction Request Payload : " + objectMapper.writeValueAsString(submitTransactionRequestDto));

            url = new URL(hmbserviceBaseUrl + submitIFTTransactionBasePath);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

//            log.info("Https Client: HttpsURLConnection");

            connection.setHostnameVerifier((hostname, session) -> true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("UserId", userId);
            connection.setRequestProperty("Password", password);
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            connection.setRequestProperty("insecure", "true");

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsString(submitTransactionRequestDto).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            String responseBody = readInputStream(connection.getInputStream());
            log.info("HMB Submit IFT Transaction Response : "+ responseBody );


            if (responseCode == 200) {
                // If successful, read the response body

                submitTransactionResponseDto = objectMapper.readValue(responseBody, SubmitTransactionResponseDto.class);

            } else {
                // If not successful, print the error message
                System.out.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return submitTransactionResponseDto;

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
    public SubmitTransactionResponseDto submitIBFTTransaction(String authToken, SubmitTransactionRequestDto submitTransactionRequestDto) throws Exception {

        URL url = null;

        SubmitTransactionResponseDto submitTransactionResponseDto = null;

        try {
            log.info("HMB Submit IBFT Transaction URL : "+hmbserviceBaseUrl + submitIBFTTransactionBasePath);
            log.info("HMB IBFT Transfer Request Payload : " + objectMapper.writeValueAsString(submitTransactionRequestDto));

            url = new URL(hmbserviceBaseUrl + submitIBFTTransactionBasePath);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

//            log.info("Https Client: HttpsURLConnection");

            connection.setHostnameVerifier((hostname, session) -> true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("UserId", userId);
            connection.setRequestProperty("Password", password);
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            connection.setRequestProperty("insecure", "true");

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsString(submitTransactionRequestDto).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            String responseBody = readInputStream(connection.getInputStream());
            log.info("HMB Submit IBFT Transaction Response : "+ responseBody );


            if (responseCode == 200) {
                // If successful, read the response body

                submitTransactionResponseDto = objectMapper.readValue(responseBody, SubmitTransactionResponseDto.class);

            } else {
                // If not successful, print the error message
                System.out.println("Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return submitTransactionResponseDto;

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
    public GetTransactionStatusResponseDto getStatus(String authToken, GetTransactionStatusRequestDto getTransactionStatusRequestDto) throws Exception {

        URL url = null;
        GetTransactionStatusResponseDto getTransactionStatusResponseDto = null;

        try {
            log.info("HMB Transaction Status Request URL : "+hmbserviceBaseUrl + getTransactionBasePath);
            log.info("HMB Transaction Status Request Payload : " + objectMapper.writeValueAsString(getTransactionStatusRequestDto));

            url = new URL(hmbserviceBaseUrl + getTransactionBasePath);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier((hostname, session) -> true);
            connection.setRequestProperty("insecure", "true");

            connection.setRequestMethod("POST");
            connection.setRequestProperty("UserId", userId);
            connection.setRequestProperty("Password", password);
            connection.setRequestProperty("Authorization", "Bearer " + authToken);

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

}
