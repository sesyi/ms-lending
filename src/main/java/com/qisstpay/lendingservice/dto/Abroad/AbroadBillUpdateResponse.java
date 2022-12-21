
package com.qisstpay.lendingservice.dto.Abroad;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AbroadBillUpdateResponse {

    @JsonProperty("Identification_parameter")
    private String identificationParameter;
    @JsonProperty("reserved")
    private String reserved;
    @JsonProperty("response_code")
    private String responseCode;

}
