
package com.qisstpay.lendingservice.dto.Abroad;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpEntity;

@Data
@Builder
@SuppressWarnings("unused")
public class AbroadInquiryRequest {

    @JsonProperty("consumer_number")
    private String consumerNumber;

}
