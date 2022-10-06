package com.qisstpay.lendingservice.dto.internal.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequestDto {

    private String userName;
    private String identityNumber;
    private String phoneNumber;
    private String amount;
}
