package com.qisstpay.lendingservice.dto.internal.response;

import com.qisstpay.lendingservice.enums.BillStatusType;
import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CollectionBillResponseDto {
    private Long               billId;
    private String             consumerId;
    private String             userName;
    private Double             amount;
    private Double             chargedAmount;
    private Double             amountAfterDueDate;
    private Timestamp          dueDate;
    private String             transactionId;
    private String             collectionTransactionId;
    private BillStatusType     billStatus;
    private String             consumerEmail;
    private String             billingMonth;
    private PaymentGatewayType gatewayType;
    private Timestamp          paidAt;
    private String             paymentStatus;
}
