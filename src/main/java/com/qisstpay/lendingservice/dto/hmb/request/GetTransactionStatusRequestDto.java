package com.qisstpay.lendingservice.dto.hmb.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetTransactionStatusRequestDto {
    @JsonProperty("MakerID")
    private String makerID;
    @JsonProperty("REFNO")
    private String refNo;
    @JsonProperty("Stan")
    private String stan;
    @JsonProperty("DateTime")
    private String dateTime;
}
