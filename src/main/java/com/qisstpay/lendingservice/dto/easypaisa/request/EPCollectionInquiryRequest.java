
package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EPCollectionInquiryRequest {

    @JsonProperty("consumer_number")
    private String consumerNumber;
    @JsonProperty("password")
    private String password;
    @JsonProperty("username")
    private String username;
}
