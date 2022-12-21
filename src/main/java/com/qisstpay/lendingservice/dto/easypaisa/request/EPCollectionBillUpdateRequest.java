
package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class EPCollectionBillUpdateRequest {

    @JsonProperty("consumer_number")
    private String consumerNumber;
    @Expose
    private String password;
    @JsonProperty("tran_auth_id")
    private String tranAuthId;
    @JsonProperty("tran_date")
    private String tranDate;
    @JsonProperty("tran_time")
    private String tranTime;
    @JsonProperty("transaction_amount")
    private String transactionAmount;
    @Expose
    private String username;
}
