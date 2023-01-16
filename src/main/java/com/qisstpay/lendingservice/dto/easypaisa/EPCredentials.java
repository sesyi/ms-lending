package com.qisstpay.lendingservice.dto.easypaisa;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EPCredentials {
    private String msisdn;
    private String pin;
}
