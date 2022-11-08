package com.qisstpay.lendingservice.enums;

public enum GenderType {

    MALE("M"),
    FEMALE("F");

    private final String code;

    GenderType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}