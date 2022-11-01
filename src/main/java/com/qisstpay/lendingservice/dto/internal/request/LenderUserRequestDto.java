package com.qisstpay.lendingservice.dto.internal.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LenderUserRequestDto {
    private Long   userId;
    private String apiKey;
    private String credentialFileUrl;
}
