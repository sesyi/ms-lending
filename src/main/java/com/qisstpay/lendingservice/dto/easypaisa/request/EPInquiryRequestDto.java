package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EPInquiryRequestDto {

    @JsonProperty("Amount")
    String amount;
    @JsonProperty("MSISDN")
    String subscriberMSISDN;
    @JsonProperty("ReceiverMSISDN")
    String receiverMSISDN;
}
