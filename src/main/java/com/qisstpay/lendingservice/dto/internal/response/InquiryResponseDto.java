package com.qisstpay.lendingservice.dto.internal.response;

import com.qisstpay.lendingservice.dto.easypaisa.response.EPInquiryResponseDto;
import com.qisstpay.lendingservice.enums.QPResponseCode;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDto {

    String result;
    QPResponseCode qpResponseCode;
    EPInquiryResponseDto epInquiryResponseDto;
    String transactionId;
}
