package com.qisstpay.lendingservice.error.errortype;

import com.qisstpay.commons.error.ErrorEnumType;

public enum BillErrorType implements ErrorEnumType<BillErrorType> {
    ENABLE_TO_GET_BILL(0, "Unable to fetch Bill Details. Invalid Bill Id"),
    INVALID_IDENTITY_NUMBER(1, "Unable to fetch Bill Details. Invalid identity number");

    private int    code;
    private String errorMessage;

    private BillErrorType(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorCode() {
        return "BILL-" + String.format("%04d", code);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
