package com.qisstpay.lendingservice.dto.qpay.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataRequestDto {
    private String                      gateway;
    private GatewayCredentialRequestDto gateway_credentials;
}
