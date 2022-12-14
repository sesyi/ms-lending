
package com.qisstpay.lendingservice.dto.easypaisa.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EPCollectionBillUpdateResponse {

    @JsonProperty("Identification_parameter")
    private String identificationParameter;
    @JsonProperty("response_code")
    private String responseCode;
    @JsonProperty("response_message")
    private String responseMessage;
    @JsonProperty("tran_auth_Id")
    private String tranAuthId;
}
