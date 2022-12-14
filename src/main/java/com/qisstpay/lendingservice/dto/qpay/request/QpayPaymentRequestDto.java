package com.qisstpay.lendingservice.dto.qpay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @JsonProperty("reference_order_id")
    private String refTransactionId;

    @JsonProperty("gateway")
    private String gateway;

    @JsonProperty("source")
    private String source;

    @JsonProperty("tokenized_card")
    private String tokenizedCard;

    @JsonProperty("card_number")
    private String cardNumber;

    @JsonProperty("cvv")
    private String cvv;

    @JsonProperty("expiry_month")
    private String expiryMonth;

    @JsonProperty("expiry_year")

    @JsonProperty("card_holder_name")
    private String cardHolderName;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("three_ds")
    private String threeDs;

    @JsonProperty("installments")
    private Integer installments;

    @JsonProperty("gateway_credentials")
    private HashMap<String, String> gatewayCredentials;

    @JsonProperty("shipping_address")
    private HashMap<String, String> shippingAddress;

    @JsonProperty("nift_otp")
    private NiftOtpRequestDto niftOtp;
}
