package com.qisstpay.lendingservice.dto.internal.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qisstpay.lendingservice.enums.TransferType;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionStateResponse {
    private String transactionId;
    private String code;
    private String state;
    private String description;

    private String userName;
    private String consumerNumber;
    private String phoneNumber;
    private String bankCode;
    private String accountNumber;
    private double amount;

}
