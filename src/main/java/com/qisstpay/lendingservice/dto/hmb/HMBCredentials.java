package com.qisstpay.lendingservice.dto.hmb;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HMBCredentials {
    private String accountTitle;
    private String accountNumber;
    private String userId;
    private String password;

}
