
package com.qisstpay.lendingservice.dto.Abroad;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class AbroadBillUpdateRequest {

    @JsonProperty("consumer_number")
    private String consumerNumber;
    @JsonProperty("tran_auth_id")
    private String tranAuthId;
    @JsonProperty("tran_date")
    private String tranDate;
    @JsonProperty("tran_time")
    private String tranTime;
    @JsonProperty("transaction_amount")
    private String transactionAmount;
}
