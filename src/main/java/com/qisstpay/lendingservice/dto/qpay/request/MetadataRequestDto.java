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

    @JsonProperty("gateway_credentials")
    private HashMap<String, String> gatewayCredentials;

    @JsonProperty("nift_transaction")
    private NiftTransactionRequestDto niftTransaction;
}
