package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqConsumerPersonalInformationResponseDto {

    @JsonProperty("NAME")
    private String name;

    @JsonProperty("FATHER_OR_HUSBAND_NAME")
    private String fatherOrHusbandName;

    @JsonProperty("GENDER")
    private String gender;

    @JsonProperty("CNIC")
    private String cnic;

    @JsonProperty("PASSPORT")
    private String passport;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("NIC")
    private String nic;

    @JsonProperty("BUSINESS_OR_PROFESSION")
    private String businessOrProfession;

    @JsonProperty("NATIONALITY")
    private String nationality;

    @JsonProperty("NTN")
    private String ntn;

    @JsonProperty("BORROWER_TYPE")
    private String borrowerType;

    @JsonProperty("CURRENT_RESIDENTIAL_ADDRESS")
    private String currentResidentialAddress;

    @JsonProperty("CURRENT_RESIDENTIAL_ADDRESS_DATE")
    private String currentResidentialAddressDate;

    @JsonProperty("PERMANENT_ADDRESS")
    private String permanentAddress;

    @JsonProperty("PERMANENT_ADDRESS_DATE")
    private String permanentAddressDate;

    @JsonProperty("PREVIOUS_RESIDENTIAL_ADDRESS")
    private String previousResidentialAddress;

    @JsonProperty("PREVIOUS_RESIDENTIAL_ADDRESS_DATE")
    private String previousResidentialAddressDate;

    @JsonProperty("EMPLOYER_OR_BUSINESS")
    private String employerOrBusiness;

    @JsonProperty("EMPLOYER_OR_BUSINESS_DATE")
    private String employerOrBusinessDate;
}
