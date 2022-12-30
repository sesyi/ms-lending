package com.qisstpay.lendingservice.error.errortype;

import com.qisstpay.commons.error.ErrorEnumType;

public enum LendingTransactionErrorType implements ErrorEnumType<LendingTransactionErrorType> {
    ENABLE_TO_GET_TRANSACTION(0, "Unable to fetch Transaction Details. Invalid Transaction Id"),
    INVALID_IDENTITY_NUMBER(1, "Unable to fetch Transaction Details. Invalid identity number"),
    INVALID_TRANSACTION_NUMBER(1, "Unable to fetch Transaction Details. Invalid transaction number");

    private int    code;
    private String errorMessage;

    private LendingTransactionErrorType(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorCode() {
        return "LENDING_TRAN-" + String.format("%04d", code);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
