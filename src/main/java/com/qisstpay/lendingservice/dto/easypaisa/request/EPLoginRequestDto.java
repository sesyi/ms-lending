
package com.qisstpay.lendingservice.dto.easypaisa.request;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class EPLoginRequestDto {

    @JsonProperty("LoginPayload")
    private String loginPayload;

    public String getLoginPayload() {
        return loginPayload;
    }

    public void setLoginPayload(String loginPayload) {
        this.loginPayload = loginPayload;
    }

}
