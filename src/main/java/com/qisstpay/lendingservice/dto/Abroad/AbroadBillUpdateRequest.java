
package com.qisstpay.lendingservice.dto.Abroad;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AbroadBillUpdateRequest {

    @SerializedName("consumer_number")
    private String consumerNumber;
    @SerializedName("tran_auth_id")
    private String tranAuthId;
    @SerializedName("tran_date")
    private String tranDate;
    @SerializedName("tran_time")
    private String tranTime;
    @SerializedName("transaction_amount")
    private String transactionAmount;

}
