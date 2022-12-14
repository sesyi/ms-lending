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
public class NiftOtpRequestDto {
    @JsonProperty("pp_BankID")
    private String bankID;

    @JsonProperty("pp_AccountNo")
    private String accountNo;

    @JsonProperty("pp_CNIC")
    private String cnic;
}
