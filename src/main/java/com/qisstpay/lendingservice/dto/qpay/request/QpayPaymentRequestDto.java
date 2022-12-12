package com.qisstpay.lendingservice.dto.qpay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QpayPaymentRequestDto {
    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("tax_amount")
    private Double taxAmount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("country")
    private String country;

    @JsonProperty("order_id")
    private String transactionId;

    @JsonProperty("gateway")
    private String gateway;

    @JsonProperty("gateway_credentials")
    private GatewayCredentialRequestDto gatewayCredentials;
}

