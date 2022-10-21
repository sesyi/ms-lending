package com.qisstpay.lendingservice.dto.internal.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDto {

    String result;
    String qpResponseCode;
    Object epResult;
    String transactionId;
}
