package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EPRequestDto extends HttpEntity {

    @JsonProperty("Amount")
    Double amount;
    @JsonProperty("MSISDN")
    String subscriberMSISDN;
    @JsonProperty("ReceiverMSISDN")
    String receiverMSISDN;

    @Override
    public String toString() {
        return "EPRequestDto{" +
                "amount=" + amount +
                ", subscriberMSISDN='" + subscriberMSISDN + '\'' +
                ", receiverMSISDN='" + receiverMSISDN + '\'' +
                '}';
    }
}
