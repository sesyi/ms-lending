package com.qisstpay.lendingservice.dto.internal.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankResponseDto {

    private String name;
    private String code;
}
