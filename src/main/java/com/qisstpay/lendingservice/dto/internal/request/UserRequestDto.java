package com.qisstpay.lendingservice.dto.internal.request;

import com.qisstpay.lendingservice.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private Long   userId;
    private String apiKey;
    private String credentialFileUrl;
    private UserType userType;
    private String userName;
}
