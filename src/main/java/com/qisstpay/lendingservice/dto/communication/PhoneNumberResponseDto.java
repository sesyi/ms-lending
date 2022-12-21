package com.qisstpay.lendingservice.dto.communication;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhoneNumberResponseDto {
    public PhoneNumberFormatBody body;
    public List<ErrorResponseDto> errors;
}
