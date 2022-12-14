package com.qisstpay.lendingservice.dto.qpay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NiftTransactionRequestDto {
    @JsonProperty("pp_OTP")
    private String otp;

    @JsonProperty("pp_RetreivalReferenceNo")
    private String transactionId;

    @JsonProperty("pp_TxnRefNo")
    private String refTransactionId;

    @JsonProperty("pp_BankID")
    private String bankId;

}
