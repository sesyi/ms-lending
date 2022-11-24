package com.qisstpay.lendingservice.dto.hmb.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitIFTTransactionRequestDto {
    private String channelID;
    private String productCode;
}
