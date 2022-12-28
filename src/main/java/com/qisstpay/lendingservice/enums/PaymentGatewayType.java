package com.qisstpay.lendingservice.enums;

public enum PaymentGatewayType {
    EASYPAISA("easypaisa"),
    NIFT("nift"),
    STRIPE("stripe"),
    UBL("ubl");

    private final String name;

    PaymentGatewayType(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
