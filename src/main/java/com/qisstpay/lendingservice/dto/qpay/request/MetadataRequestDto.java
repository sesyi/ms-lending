package com.qisstpay.lendingservice.dto.qpay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MetadataRequestDto {
    @JsonProperty("gateway")
    private String gateway;

    @JsonProperty("source")
    private String source;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("order_id")
    private String transactionId;

    @JsonProperty("reference_order_id")
    private String refTransactionId;

    @JsonProperty("transaction_id")
    private String serviceTransactionId;

    @JsonProperty("gateway_credentials")
    private HashMap<String, String> gatewayCredentials;

    @JsonProperty("nift_transaction")
    private NiftTransactionRequestDto niftTransaction;
}
