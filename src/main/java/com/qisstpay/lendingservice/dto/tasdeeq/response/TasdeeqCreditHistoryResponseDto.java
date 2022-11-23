package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqCreditHistoryResponseDto {

    @JsonProperty("LOAN_SERIAL_NUMBER")
    private String loan_serial_number;

    @JsonProperty("MONTH_NAME")
    private String monthName;

    @JsonProperty("PLUS_30")
    private String plus30;

    @JsonProperty("PLUS_60")
    private String plus60;

    @JsonProperty("PLUS_90")
    private String plus90;

    @JsonProperty("PLUS_120")
    private String plus120;

    @JsonProperty("PLUS_150")
    private String plus150;

    @JsonProperty("PLUS_180")
    private String plus180;

    @JsonProperty("MFI_DEFAULT")
    private String mfiDefault;

    @JsonProperty("LATE_PMT_DAYS")
    private String latePmtDays;
}
