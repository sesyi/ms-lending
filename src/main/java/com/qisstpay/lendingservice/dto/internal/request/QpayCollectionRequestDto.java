package com.qisstpay.lendingservice.dto.internal.request;

import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QpayCollectionRequestDto {
    private Long               billId;
    private String             accountNumber;
    private String             customerEmail;
    private String             cardHolderName;
    private String             cardNumber;
    private String             cvv;
    private String             expiryMonth;
    private String             expiryYear;
    private String             bankID;
    private String             cnic;
    private PaymentGatewayType gatewayType;
}
