package com.qisstpay.lendingservice.dto.internal.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qisstpay.lendingservice.enums.GenderType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditScoreRequestDto {

    @JsonProperty(value = "cnic", required = true)
    @NotNull(message = "Please provide cnic")
    private String cnic;

    @JsonProperty(value = "loanAmount", required = true)
    @NotNull(message = "Please provide loanAmount")
    private Integer    loanAmount;

    @JsonProperty(value = "fullName", required = true)
    @NotNull(message = "Please provide fullName")
    private String     fullName;

    @JsonProperty(value = "dateOfBirth", required = true)
    @NotNull(message = "Please provide dateOfBirth")
    private String     dateOfBirth;

    @JsonProperty(value = "city", required = true)
    @NotNull(message = "Please provide city")
    private String     city;

    @JsonProperty(value = "phoneNumber", required = true)
    @NotNull(message = "Please provide phoneNumber")
    private String     phoneNumber;

    @JsonProperty(value = "currentAddress", required = true)
    @NotNull(message = "Please provide currentAddress")
    private String     currentAddress;

    @JsonProperty(value = "fatherHusbandName", required = true)
    @NotNull(message = "Please provide fatherHusbandName")
    private String     fatherHusbandName;

    @JsonProperty(value = "gender", required = true)
    @NotNull(message = "Please provide gender")
    private GenderType gender;
}