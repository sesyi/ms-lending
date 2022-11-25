package com.qisstpay.lendingservice.dto.easypaisa.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EPInquiryResponseDto {

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

    @Override
    public String toString() {
        return "EPInquiryResponseDto{" +
                "responseCode='" + responseCode + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                ", transactionReference='" + transactionReference + '\'' +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", tax='" + tax + '\'' +
                ", fee='" + fee + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
