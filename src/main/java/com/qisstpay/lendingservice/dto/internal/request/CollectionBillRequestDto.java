package com.qisstpay.lendingservice.dto.internal.request;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CollectionBillRequestDto {
    private Double    amount;
    private Double    amountAfterDueDate;
    private String    transactionId;
    private String    billingMonth;
    private Timestamp dueDate;
    private String    consumerCnic;
    private String    consumerPhoneNumber;
    private String    consumerName;
}
