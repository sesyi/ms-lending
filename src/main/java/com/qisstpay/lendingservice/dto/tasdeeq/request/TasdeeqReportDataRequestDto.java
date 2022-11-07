
package com.qisstpay.lendingservice.dto.tasdeeq.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqReportDataRequestDto {

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("city")
    private String city;

    @JsonProperty("loanAmount")
    private String loanAmount;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("currentAddress")
    private String currentAddress;

    @JsonProperty("CNIC")
    private String cnic;

    @JsonProperty("fatherHusbandName")
    private String fatherHusbandName;
}
