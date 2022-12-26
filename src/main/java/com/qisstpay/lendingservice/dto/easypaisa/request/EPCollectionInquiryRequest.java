
package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class EPCollectionInquiryRequest {

    @NotNull
    @JsonProperty("Consumer_Number")
    private String consumerNumber;
    @JsonProperty("Password")
    private String password;
    @JsonProperty("Username")
    private String username;
    @NotNull
    @JsonProperty("Bank_Mnemonic")
    private String bankMnemonic;
    @JsonProperty("Reserved")
    private String reserved;
}
