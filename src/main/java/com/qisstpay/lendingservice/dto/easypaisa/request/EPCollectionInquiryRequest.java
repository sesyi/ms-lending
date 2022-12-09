
package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class EPCollectionInquiryRequest {

    @SerializedName("consumer_number")
    private String consumerNumber;
    @SerializedName("password")
    private String password;
    @SerializedName("username")
    private String username;
}
