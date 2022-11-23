
package com.qisstpay.lendingservice.dto.tasdeeq.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqReportDataRequestDto {

    @JsonProperty("loanAmount")
    private String loanAmount;

    @JsonProperty("CNIC")
    private String cnic;

}
