package com.qisstpay.lendingservice.enums;

public enum AbroadResponseCode {
    SUCCESSFUL_EXECUTION("00", "Successful execution"),
    EXCEPTION_OCCURRED("-1", "Exception occurred"),
    ABROAD_INQUIRY_FAILED("202","Abroad inquiry failed");

    private final String code;
    private final String description;

    AbroadResponseCode(String code, String description) {
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