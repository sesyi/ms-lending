
package com.qisstpay.lendingservice.dto.Abroad;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class AbroadInquiryRequest {

    @SerializedName("consumer_number")
    private String consumerNumber;

}
