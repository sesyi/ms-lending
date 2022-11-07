
package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqResponseDto {
    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("messageCode")
    private String messageCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private Object data;

}
