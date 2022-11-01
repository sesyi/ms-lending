package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqConsumerReportResponseDto {

    @JsonProperty("ResponseCode")
    private String responseCode;
    @JsonProperty("ResponseMessage")
    private String responseMessage;
    @JsonProperty("TransactionReference")
    private String transactionReference;
    @JsonProperty("TransactionStatus")
    private String transactionStatus;
    @JsonProperty("Tax")
    private String tax;
    @JsonProperty("Fee")
    String fee;
    @JsonProperty("Name")
    String name;
}
