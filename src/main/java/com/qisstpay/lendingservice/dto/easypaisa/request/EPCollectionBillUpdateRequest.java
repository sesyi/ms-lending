
package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
public class EPCollectionBillUpdateRequest {

    @NotNull
    @JsonProperty("consumer_number")
    private String consumerNumber;
    @Expose
    @JsonProperty("password")
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
    @NotNull
    @JsonProperty("username")
    private String username;
    @NotNull
    @JsonProperty("bank_mnemonic")
    private String bankMnemonic;
    @JsonProperty("reserved")
    private String reserved;
}
