package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqPersonalGuaranteesResponseDto {

    @JsonProperty("PRODUCT")
    private String product;

    @JsonProperty("PRINCIPAL_BORROWER_NAME")
    private String principalBorrowerName;

    @JsonProperty("PRINCIPAL_BORROWER_CNIC")
    private String principalBorrowerCnic;

    @JsonProperty("DATE_OF_INVOCATION")
    private Integer dateOfInvocation;

    @JsonProperty("GUARANTEE_DATE")
    private String guaranteeDate;

    @JsonProperty("GUARANTEE_AMOUNT")
    private Integer guaranteeAmount;

}
