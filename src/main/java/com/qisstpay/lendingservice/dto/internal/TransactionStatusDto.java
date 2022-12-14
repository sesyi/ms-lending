package com.qisstpay.lendingservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qisstpay.lendingservice.enums.TransactionState;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusDto {
    private TransactionState state;
    private String description;
}
