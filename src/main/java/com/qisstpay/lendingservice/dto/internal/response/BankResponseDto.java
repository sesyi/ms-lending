package com.qisstpay.lendingservice.dto.internal.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BankResponseDto {

    private String name;
    private String bankCode;
}
