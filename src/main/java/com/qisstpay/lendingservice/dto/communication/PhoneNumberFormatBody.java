package com.qisstpay.lendingservice.dto.communication;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PhoneNumberFormatBody {
    public String fullPhoneNumber;
    public String countryCode;
    public String nationalNumber;
}
