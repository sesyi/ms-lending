package com.qisstpay.lendingservice.dto.hmb.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTransactionStatusResponseDto {
    @JsonProperty("ResponseCode")
    private String ResponseCode;
    @JsonProperty("ResponseDescription")
    private String ResponseDescription;
}
