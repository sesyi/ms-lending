
package com.qisstpay.lendingservice.dto.Abroad;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbroadInquiryResponse {

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
    @JsonProperty("tran_auth_id")
    private String tranAuthId;
}
