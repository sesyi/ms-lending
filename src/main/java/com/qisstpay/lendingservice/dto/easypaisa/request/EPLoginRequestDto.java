
package com.qisstpay.lendingservice.dto.easypaisa.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.springframework.http.HttpEntity;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
@ToString
public class EPLoginRequestDto extends HttpEntity {

    @JsonProperty("LoginPayload")
    private String loginPayload;

    public String getLoginPayload() {
        return loginPayload;
    }

    public void setLoginPayload(String loginPayload) {
        this.loginPayload = loginPayload;
    }

    @Override
    public String toString() {
        return "EPLoginRequestDto{" +
                "loginPayload='" + loginPayload + '\'' +
                '}';
    }
}
