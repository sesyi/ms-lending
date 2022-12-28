package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqCreditScoreDataResponseDto {

    @JsonProperty("Score")
    private Integer score;

    @JsonProperty("Month")
    private String month;

    @JsonProperty("Remarks")
    private String remarks;
}
