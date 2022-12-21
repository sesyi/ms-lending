package com.qisstpay.lendingservice.dto.internal.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageResponseDto {
    private String  message;
    private Boolean success;
}
