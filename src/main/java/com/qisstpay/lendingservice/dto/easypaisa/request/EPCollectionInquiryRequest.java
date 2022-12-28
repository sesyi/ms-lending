
package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class EPCollectionInquiryRequest {

    @NotNull
    @JsonProperty("Consumer_number")
    private String consumerNumber;
    @Expose
    @JsonProperty("password")
    private String password;
    @Expose
    @NotNull
    @JsonProperty("username")
    private String username;
    @NotNull
    @JsonProperty("Bank_Mnemonic")
    private String bankMnemonic;
    @JsonProperty("Reserved")
    private String reserved;
}
