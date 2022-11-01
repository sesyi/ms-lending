package com.qisstpay.lendingservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qisstpay.commons.response.CustomResponse;
import com.qisstpay.lendingservice.dto.internal.request.TransferRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.TransactionStateResponse;
import com.qisstpay.lendingservice.dto.internal.response.TransferResponseDto;
import com.qisstpay.lendingservice.service.LendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/lending/v1")
@RequiredArgsConstructor
public class LendingController {


    private static final String TRANSFER = "/transfer";
    private static final String STATUS   = "/status/{transactionId}";

    private final LendingService lendingService;

    @PostMapping(TRANSFER)
    public CustomResponse<TransferResponseDto> transfer(@RequestBody TransferRequestDto transferRequestDto) throws JsonProcessingException {
        return CustomResponse.CustomResponseBuilder.<TransferResponseDto>builder()
                .body(lendingService.transfer(transferRequestDto)).build();
    }

    @GetMapping(STATUS)
    public CustomResponse<TransactionStateResponse> status(@PathVariable("transactionId") String transactionId) {
        return CustomResponse.CustomResponseBuilder.<TransactionStateResponse>builder()
                .body(lendingService.checkStatus(transactionId)).build();
    }
}
