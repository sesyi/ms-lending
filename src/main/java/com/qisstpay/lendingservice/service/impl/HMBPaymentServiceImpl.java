package com.qisstpay.lendingservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitIFTTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.response.GetTokenResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.SubmitIBFTTransactionResponseDto;
import com.qisstpay.lendingservice.dto.hmb.response.SubmitIFTTransactionResponseDto;
import com.qisstpay.lendingservice.service.HMBPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class HMBPaymentServiceImpl implements HMBPaymentService {

    private String hmbserviceBaseUrl = "http://172.27.81.112";
    
    private String getTokenAPIBasePath = "/TransPaymentAPI/Transaction/GetToken";
    private String submitIFTTransactionBasePath = "/TransPaymentAPI/Transaction/TransSubmit";
    private String submitIBFTTransactionBasePath = "/TransPaymentAPI/Transaction/TransSubmit";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public GetTokenResponseDto getToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("UserId", "EFAPI");
        headers.add("Password", "CRA");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        GetTokenResponseDto getTokenResponseDto = null;

        try{
            String response = restTemplate.exchange(hmbserviceBaseUrl + getTokenAPIBasePath, HttpMethod.GET, requestEntity, String.class).getBody();
            getTokenResponseDto = objectMapper.readValue(response, GetTokenResponseDto.class);
        }catch (Exception e){

        }
        return getTokenResponseDto;
    }

    @Override
    public SubmitIFTTransactionResponseDto submitIFTTransaction(String authToken, SubmitIFTTransactionRequestDto submitIFTTransactionRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("UserId", "EFAPI");
        headers.add("Password", "CRA");
        headers.add("Authorization", "Bearer "+authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        SubmitIFTTransactionResponseDto submitIFTTransactionResponseDto = null;

        try{
            String response = restTemplate.exchange(hmbserviceBaseUrl + submitIFTTransactionBasePath, HttpMethod.POST, requestEntity, String.class).getBody();
            submitIFTTransactionResponseDto =  objectMapper.readValue(response, SubmitIFTTransactionResponseDto.class);
        }catch (Exception e){

        }

        return submitIFTTransactionResponseDto;
    }

    @Override
    public SubmitIBFTTransactionResponseDto submitIBFTTransaction(String authToken, SubmitIBFTTransactionResponseDto submitIBFTTransactionResponseDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("UserId", "EFAPI");
        headers.add("Password", "CRA");
        headers.add("Authorization", "Bearer "+authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        SubmitIBFTTransactionResponseDto submitIFTTransactionResponseDto = null;

        try{
            String response = restTemplate.exchange(hmbserviceBaseUrl + submitIFTTransactionBasePath, HttpMethod.POST, requestEntity, String.class).getBody();
            submitIBFTTransactionResponseDto =  objectMapper.readValue(response, SubmitIBFTTransactionResponseDto.class);
        }catch (Exception e){

        }

        return submitIBFTTransactionResponseDto;
    }
}
