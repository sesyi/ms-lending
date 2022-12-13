package com.qisstpay.lendingservice.dto.internal.request;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionBillRequestDto {
    private Double    amount;
    private Double    amountAfterDueDate;
    private String    userName;
    private String    identityNumber;
    private Timestamp dueDate;
}