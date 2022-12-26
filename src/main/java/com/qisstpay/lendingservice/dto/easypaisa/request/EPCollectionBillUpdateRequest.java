
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
    @JsonProperty("Consumer_Number")
    private String consumerNumber;
//    @Expose
//    private String password;
    @JsonProperty("Tran_Auth_Id")
    private String tranAuthId;
    @JsonProperty("Tran_Date")
    private String tranDate;
    @JsonProperty("Tran_Time")
    private String tranTime;
    @JsonProperty("Transaction_Amount")
    private String transactionAmount;
//    @Expose
//    private String username;
    @NotNull
    @JsonProperty("Bank_Mnemonic")
    private String bankMnemonic;
    @JsonProperty("Reserved")
    private String reserved;
}
