package com.qisstpay.lendingservice.dto.tasdeeq.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
public class TasdeeqAuthRequestDto {

    @JsonProperty("UserName")
    String userName;

    @JsonProperty("Password")
    String password;

}
