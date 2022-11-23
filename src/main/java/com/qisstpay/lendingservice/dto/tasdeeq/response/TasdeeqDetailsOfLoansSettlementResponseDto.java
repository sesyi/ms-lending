package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqDetailsOfLoansSettlementResponseDto {

    @JsonProperty("PRODUCT")
    private String product;

    @JsonProperty("TOTAL_LIMIT")
    private String totalLimit;

    @JsonProperty("APPROVAL_DATE")
    private String approvalDate;

    @JsonProperty("RELATIONSHIP_DATE")
    private String relationshipDate;

    @JsonProperty("MATURITY_DATE")
    private String maturityDate;

    @JsonProperty("DATE_OF_SETTLEMENT")
    private String dateOfSettlement;

}
