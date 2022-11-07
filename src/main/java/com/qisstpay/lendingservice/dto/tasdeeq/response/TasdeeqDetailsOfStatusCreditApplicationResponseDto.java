package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqDetailsOfStatusCreditApplicationResponseDto {

    @JsonProperty("PRODUCT")
    private String product;

    @JsonProperty("FINANCIAL_INSTITUTION")
    private String financialInstitution;

    @JsonProperty("DATE_OF_APPLICATION")
    private String dateOfApplication;

    @JsonProperty("AMOUNT_OF_FACILITY")
    private String amountOfFacility;

    @JsonProperty("STATUS")
    private String status;

}
