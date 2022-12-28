package com.qisstpay.lendingservice.dto.internal.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionStateResponse {
    private String userName;
    private String identityNumber;
    private String phoneNumber;
    private String accountNumber;
    private double amount;
    private String transactionId;
    private String code;
    private String state;
    private String description;
}
