package com.qisstpay.lendingservice.dto.internal.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchTitleResponseDto {

    private String bankCode;
    private String accountNumber;
    private String accountTitle;
}
