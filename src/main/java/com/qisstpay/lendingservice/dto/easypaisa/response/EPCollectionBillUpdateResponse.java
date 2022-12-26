
package com.qisstpay.lendingservice.dto.easypaisa.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class EPCollectionBillUpdateResponse {

    @JsonProperty("Identification_parameter")
    private String identificationParameter;
    @JsonProperty("Response_Code")
    private String responseCode;
    @JsonProperty("Response_message")
    private String responseMessage;
    @JsonProperty("Reserved")
    private String reserved;
}
