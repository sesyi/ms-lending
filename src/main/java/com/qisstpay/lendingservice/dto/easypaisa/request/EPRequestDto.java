package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EPRequestDto {

    @JsonProperty("Amount")
    Double amount;
    @JsonProperty("MSISDN")
    String subscriberMSISDN;
    @JsonProperty("ReceiverMSISDN")
    String receiverMSISDN;
}
