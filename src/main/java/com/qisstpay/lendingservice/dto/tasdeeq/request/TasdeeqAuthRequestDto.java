package com.qisstpay.lendingservice.dto.tasdeeq.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqAuthRequestDto {

    @JsonProperty("Amount")
    Double amount;
    @JsonProperty("MSISDN")
    String subscriberMSISDN;
    @JsonProperty("ReceiverMSISDN")
    String receiverMSISDN;
}
