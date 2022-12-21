
package com.qisstpay.lendingservice.dto.Abroad;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpEntity;

@Data
@Builder
@SuppressWarnings("unused")
@ToString
public class AbroadInquiryRequest {

    @JsonProperty("consumer_number")
    private String consumerNumber;

}
