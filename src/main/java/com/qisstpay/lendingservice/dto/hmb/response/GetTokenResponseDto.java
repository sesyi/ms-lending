package com.qisstpay.lendingservice.dto.hmb.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetTokenResponseDto {
    @JsonProperty("Token")
    private String token;
    @JsonProperty("ValidTill")
    private String validTill;
}
