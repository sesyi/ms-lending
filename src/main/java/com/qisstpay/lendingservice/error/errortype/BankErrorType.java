package com.qisstpay.lendingservice.error.errortype;

import com.qisstpay.commons.error.ErrorEnumType;

public enum BankErrorType implements ErrorEnumType<BankErrorType> {
    ENABLE_TO_GET_BANK(0, "Unable to fetch Bank Details.");

    private int    code;
    private String errorMessage;

    private BankErrorType(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorCode() {
        return "BANK-" + String.format("%04d", code);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
