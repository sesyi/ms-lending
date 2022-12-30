package com.qisstpay.lendingservice.dto.qpay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SourceMetadataRequestDto {
    @JsonProperty("redirect_url")
    private String redirectURL;
}
