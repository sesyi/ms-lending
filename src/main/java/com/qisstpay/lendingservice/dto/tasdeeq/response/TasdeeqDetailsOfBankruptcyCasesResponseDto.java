package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqDetailsOfBankruptcyCasesResponseDto {

    @JsonProperty("COURT_NAME")
    private String courtName;

    @JsonProperty("DECLARATION_DATE")
    private String declarationDate;

}
