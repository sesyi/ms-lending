package com.qisstpay.lendingservice.dto.qpay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QpayCaptureRequestDto {
    @JsonProperty("id")
    private String transactionId;

    @JsonProperty("metadata")
    private MetadataRequestDto metadata;

}

