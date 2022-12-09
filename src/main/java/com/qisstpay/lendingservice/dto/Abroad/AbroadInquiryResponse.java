
package com.qisstpay.lendingservice.dto.Abroad;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class AbroadInquiryResponse {

    @SerializedName("amount_after_due_date")
    private String mAmountAfterDueDate;
    @SerializedName("amount_paid")
    private String mAmountPaid;
    @SerializedName("amount_within_due_date")
    private String mAmountWithinDueDate;
    @SerializedName("bill_status")
    private String mBillStatus;
    @SerializedName("billing_month")
    private String mBillingMonth;
    @SerializedName("consumer_name")
    private String mConsumerName;
    @SerializedName("date_paid")
    private Object mDatePaid;
    @SerializedName("due_date")
    private String mDueDate;
    @SerializedName("response_code")
    private String mResponseCode;
    @SerializedName("tran_auth_id")
    private String mTranAuthId;

    public String getAmountAfterDueDate() {
        return mAmountAfterDueDate;
    }

    public void setAmountAfterDueDate(String amountAfterDueDate) {
        mAmountAfterDueDate = amountAfterDueDate;
    }

    public String getAmountPaid() {
        return mAmountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        mAmountPaid = amountPaid;
    }

    public String getAmountWithinDueDate() {
        return mAmountWithinDueDate;
    }

    public void setAmountWithinDueDate(String amountWithinDueDate) {
        mAmountWithinDueDate = amountWithinDueDate;
    }

    public String getBillStatus() {
        return mBillStatus;
    }

    public void setBillStatus(String billStatus) {
        mBillStatus = billStatus;
    }

    public String getBillingMonth() {
        return mBillingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        mBillingMonth = billingMonth;
    }

    public String getConsumerName() {
        return mConsumerName;
    }

    public void setConsumerName(String consumerName) {
        mConsumerName = consumerName;
    }

    public Object getDatePaid() {
        return mDatePaid;
    }

    public void setDatePaid(Object datePaid) {
        mDatePaid = datePaid;
    }

    public String getDueDate() {
        return mDueDate;
    }

    public void setDueDate(String dueDate) {
        mDueDate = dueDate;
    }

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public String getTranAuthId() {
        return mTranAuthId;
    }

    public void setTranAuthId(String tranAuthId) {
        mTranAuthId = tranAuthId;
    }

}
