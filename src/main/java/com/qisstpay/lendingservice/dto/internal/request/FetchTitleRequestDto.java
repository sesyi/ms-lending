package com.qisstpay.lendingservice.dto.internal.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qisstpay.lendingservice.enums.TransferType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchTitleRequestDto {

    private String bankCode;
    private String accountNumber;
}
