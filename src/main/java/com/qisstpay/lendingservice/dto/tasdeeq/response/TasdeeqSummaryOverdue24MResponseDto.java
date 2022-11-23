package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqSummaryOverdue24MResponseDto {

    @JsonProperty("PLUS_30_24M")
    private String plus_30_24M;

    @JsonProperty("PLUS_60_24M")
    private String plus_60_24M;

    @JsonProperty("PLUS_90_24M")
    private String plus_90_24M;

    @JsonProperty("PLUS_120_24M")
    private String plus_120_24M;

    @JsonProperty("PLUS_150_24M")
    private String plus_150_24M;

    @JsonProperty("PLUS_180_24M")
    private String plus_180_24M;

    @JsonProperty("MFI_DEFAULT")
    private String mfi_default;
}
