package com.qisstpay.lendingservice.dto.hmb.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitTransactionResponseDto {
    @JsonProperty("ResponseCode")
    private String ResponseCode;
    @JsonProperty("ResponseDescription")
    private String ResponseDescription;
}
