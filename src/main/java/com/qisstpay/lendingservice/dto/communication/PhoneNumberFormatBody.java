package com.qisstpay.lendingservice.dto.communication;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class PhoneNumberFormatBody {
    public String fullPhoneNumber;
    public String countryCode;
    public String nationalNumber;
}
