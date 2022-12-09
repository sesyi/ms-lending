
package com.qisstpay.lendingservice.dto.Abroad;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class AbroadBillUpdateResposne {

    @SerializedName("Identification_parameter")
    private Object identificationParameter;
    @SerializedName("reserved")
    private Object reserved;
    @SerializedName("response_code")
    private String responseCode;

}
