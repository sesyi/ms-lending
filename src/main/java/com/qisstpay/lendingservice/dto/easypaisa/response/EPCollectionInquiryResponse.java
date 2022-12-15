
package com.qisstpay.lendingservice.dto.easypaisa.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EPCollectionInquiryResponse {

    @JsonProperty("amount_after_due_date")
    private String amountAfterDueDate;
    @JsonProperty("amount_paid")
    private String amountPaid;
    @JsonProperty("amount_within_due_date")
    private String amountWithinDueDate;
    @JsonProperty("bill_status")
    private String billStatus;
    @JsonProperty("billing_month")
    private String billingMonth;
    @JsonProperty("consumer_name")
    private String consumerName;
    @JsonProperty("date_paid")
    private String datePaid;
    @JsonProperty("due_date")
    private String dueDate;
    @JsonProperty("response_code")
    private String responseCode;
    @JsonProperty("response_message")
    private String responseMessage;
    @JsonProperty("tran_auth_Id")
    private String tranAuthId;

}
