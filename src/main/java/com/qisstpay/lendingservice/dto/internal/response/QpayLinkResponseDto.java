package com.qisstpay.lendingservice.dto.internal.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QpayLinkResponseDto {
    private String  message;
    private String  qpayLink;
    private Boolean success;
}
