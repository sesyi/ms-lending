package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqCoborrowerDetailResponseDto {

    @JsonProperty("NAME")
    private String name;

    @JsonProperty("CNIC")
    private String cnic;

}
