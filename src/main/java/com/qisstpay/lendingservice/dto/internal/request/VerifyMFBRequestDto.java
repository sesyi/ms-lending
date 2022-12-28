package com.qisstpay.lendingservice.dto.internal.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyMFBRequestDto {
    private String username;
    private String password;
}
