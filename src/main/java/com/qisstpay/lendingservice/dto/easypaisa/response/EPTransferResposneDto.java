
package com.qisstpay.lendingservice.dto.easypaisa.response;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class EPTransferResposneDto {

    @JsonProperty("Balance")
    private String balance;
    @JsonProperty("Fee")
    private String fee;
    @JsonProperty("ResponseCode")
    private String responseCode;
    @JsonProperty("ResponseMessage")
    private String responseMessage;
    @JsonProperty("TransactionReference")
    private String transactionReference;
    @JsonProperty("TransactionStatus")
    private String transactionStatus;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

}
