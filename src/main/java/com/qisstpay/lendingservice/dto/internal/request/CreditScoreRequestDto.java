package com.qisstpay.lendingservice.dto.internal.request;

import com.qisstpay.lendingservice.enums.GenderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditScoreRequestDto {
    private String     cnic;
    private Integer    loanAmount;
    private String     fullName;
    private String     dateOfBirth;
    private String     city;
    private String     phoneNumber;
    private String     currentAddress;
    private String     fatherHusbandName;
    private GenderType gender;
}