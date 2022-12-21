package com.qisstpay.lendingservice.enums;

public enum TransferState {
    TRANSFER_SUCCESS("1","SUCCESS", "Transfer completed"),
    SOMETHING_WENT_WRONG("-1","FAILURE", "Something went wrong"),
    UNSC_CONTROL("101","FAILURE", "UNSC control"),
    AML_CONTROL("102","FAILURE", "AML control"),
    EP_LOGIN_FAILED("103","FAILURE","Payment channel Authorization failed"),
    EP_INQUIRY_FAILED("104","FAILURE","EP inquiry failed"),
    TRANSFER_FAILED("105","FAILURE","Transfer failed"),
    TRXN_FETCH_FAILED("106","FAILURE","Trxn fetch failed"),
    INSUFFICIENT_FUNDS("107","FAILURE", "Insufficient funds"),
    TRANSFER_LIMIT_EXCEEDED("108","FAILURE", "Transfer limit exceeded"),
    CURRENCY_MISMATCH("109","FAILURE", "Currency mismatch for recipient"),
    RECIPIENT_ACCOUNT_NOT_FOUND("110","FAILURE", "Recipient account is not found"),
    RECIPIENT_ACCOUNT_INACTIVE("111","FAILURE", "Recipient account is inactive"),
    RELEASER_AUTHORIZATION_NEEDED("112","PENDING","Transfer to be authorized by releaser");


    private final String code;
    private final String state;
    private final String description;

    TransferState(String code, String state, String description) {
        this.code = code;
        this.state = state;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public String toString() {
        return code + ": " + state + ": " + description;
    }
}
