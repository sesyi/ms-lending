
package com.qisstpay.lendingservice.dto.tasdeeq.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class TasdeeqConsumerReportRequestDto {

    @JsonProperty("LoginPayload")
    private String loginPayload;

    public String getLoginPayload() {
        return loginPayload;
    }

    public void setLoginPayload(String loginPayload) {
        this.loginPayload = loginPayload;
    }

}
