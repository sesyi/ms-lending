package com.qisstpay.lendingservice.dto.internal.request;

import com.qisstpay.lendingservice.enums.TransferType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    private String userName;
    private double amount;
    private TransferType type;

    private String identityNumber;
    private String phoneNumber;

    private String accountNo;

}
