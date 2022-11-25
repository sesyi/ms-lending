package com.qisstpay.lendingservice.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumberResponseDto {
    public PhoneNumberFormatBody body;
    public List<ErrorResponseDto> errors;
}
