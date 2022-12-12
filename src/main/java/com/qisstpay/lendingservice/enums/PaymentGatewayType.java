package com.qisstpay.lendingservice.enums;

public enum PaymentGatewayType {
    EASYPAISA("TMFB", "easypaisa"),
    NIFT("nift", "nift"),
    STRIP("stripe", "stripe");

    private final String code;
    private final String name;

    PaymentGatewayType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
