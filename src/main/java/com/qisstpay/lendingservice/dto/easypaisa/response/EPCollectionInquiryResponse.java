
package com.qisstpay.lendingservice.dto.easypaisa.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EPCollectionInquiryResponse {

    @JsonProperty("amount_after_dueDate")
    private String amountAfterDueDate;
    @JsonProperty("amount_paid")
    private String amountPaid;
    @JsonProperty("amount_within_dueDate")
    private String amountWithinDueDate;
    @JsonProperty("bill_status")
    private String billStatus;
    @JsonProperty("billing_month")
    private String billingMonth;
    @JsonProperty("consumer_Detail")
    private String consumerName;
    @JsonProperty("date_paid")
    private String datePaid;
    @JsonProperty("due_date")
    private String dueDate;
    @JsonProperty("response_Code")
    private String responseCode;
    @JsonProperty("response_message")
    private String responseMessage;
    @JsonProperty("tran_auth_Id")
    private String tranAuthId;
    @JsonProperty("reserved")
    private String reserved;
}
