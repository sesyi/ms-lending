package com.qisstpay.lendingservice.enums;

public enum QPResponseCode {
    SUCCESSFUL_EXECUTION("1", "Successful execution"),
    EXCEPTION_OCCURRED("-1", "Exception occurred"),
    UNSC_CONTROL("101", "UNSC control"),
    AML_CONTROL("102", "AML control"),
    EP_LOGIN_FAILED("103","EP login failed"),
    EP_INQUIRY_FAILED("104","EP inquiry failed"),
    TRANSFER_FAILED("105","Transfer failed"),
    TRXN_FETCH_FAILED("106","Trxn fetch failed");

    private final String code;
    private final String description;

    QPResponseCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}