package com.qisstpay.lendingservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tasdeeq/v1")
@RequiredArgsConstructor
public class TasdeeqController {

    private static final String CREDIT_SCORE = "/score";

//    @PostMapping(CREDIT_SCORE)
//    public CustomResponse<TransferResponseDto> getScore(@RequestBody TransferRequestDto transferRequestDto) {
//        return CustomResponse.CustomResponseBuilder.<TransferResponseDto>builder()
//                .body(lendingService.transfer(transferRequestDto)).build();
//    }
}
