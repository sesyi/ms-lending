package com.qisstpay.lendingservice.dto.tasdeeq.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class TasdeeqConsumerReportRequestDto {

    @JsonProperty("reportDataObj")
    TasdeeqReportDataRequestDto reportDataObj;

}
