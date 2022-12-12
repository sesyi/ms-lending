package com.qisstpay.lendingservice.error.errortype;

import com.qisstpay.commons.error.ErrorEnumType;

public enum ConsumerAccountErrorType implements ErrorEnumType<ConsumerAccountErrorType> {
    ENABLE_TO_GET_TRANSACTION(0, "Unable to fetch account Details.");

    private int    code;
    private String errorMessage;

    private ConsumerAccountErrorType(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorCode() {
        return "CONSUMER_ACC-" + String.format("%04d", code);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
