package com.qisstpay.lendingservice.dto.internal.request;

import com.qisstpay.lendingservice.enums.UserType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRequestDto {
    private Long   userId;
    private String apiKey;
    private String credentialFileUrl;
    private UserType userType;
    private String userName;
}
