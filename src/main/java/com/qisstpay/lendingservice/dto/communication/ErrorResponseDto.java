package com.qisstpay.lendingservice.dto.communication;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorResponseDto {
    private String errorCode;
    private String errorMessage;

}
