
package com.qisstpay.lendingservice.dto.easypaisa.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class EPCollectionInquiryResponse {

    @SerializedName("amount_after_due_date")
    private String amountAfterDueDate;
    @SerializedName("amount_paid")
    private String amountPaid;
    @SerializedName("amount_within_due_date")
    private String amountWithinDueDate;
    @SerializedName("bill_status")
    private String billStatus;
    @SerializedName("billing_month")
    private String billingMonth;
    @SerializedName("consumer_name")
    private String consumerName;
    @SerializedName("date_paid")
    private String datePaid;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("response_code")
    private String responseCode;
    @SerializedName("tran_auth_Id")
    private String tranAuthId;

}
