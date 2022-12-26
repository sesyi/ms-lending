
package com.qisstpay.lendingservice.dto.easypaisa.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class EPCollectionInquiryResponse {

    @JsonProperty("Amount_After_DueDate")
    private String amountAfterDueDate;
    @JsonProperty("Amount_Paid")
    private String amountPaid;
    @JsonProperty("Amount_Within_DueDate")
    private String amountWithinDueDate;
    @JsonProperty("Bill_Status")
    private String billStatus;
    @JsonProperty("Billing_Month")
    private String billingMonth;
    @JsonProperty("Consumer_Detail")
    private String consumerName;
    @JsonProperty("Date_Paid")
    private String datePaid;
    @JsonProperty("Due_Date")
    private String dueDate;
    @JsonProperty("Response_Code")
    private String responseCode;
    @JsonProperty("response_message")
    private String responseMessage;
    @JsonProperty("Tran_Auth_Id")
    private String tranAuthId;
    @JsonProperty("Reserved")
    private String reserved;
}
