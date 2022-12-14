package com.qisstpay.lendingservice.error.errortype;

import com.qisstpay.commons.error.ErrorEnumType;

public enum PaymentErrorType implements ErrorEnumType<PaymentErrorType> {
    ENABLE_TO_CAPTURE(0, "This PaymentIntent could not be captured because it has a status of requires_action. Only a PaymentIntent with one of the following statuses may be captured: requires_capture."),
    ENABLE_TO_GET_STATUS(1,"Need to start payment process first");
    private int    code;
    private String errorMessage;

    private PaymentErrorType(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorCode() {
        return "QPAY-" + String.format("%04d", code);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
