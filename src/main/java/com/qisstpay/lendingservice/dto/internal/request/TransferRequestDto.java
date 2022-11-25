package com.qisstpay.lendingservice.dto.internal.request;

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
    private String identityNumber;
    private String phoneNumber;
    private double amount;

    @Override
    public String toString() {
        return "TransferRequestDto{" +
                "userName='" + userName + '\'' +
                ", identityNumber='" + identityNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
