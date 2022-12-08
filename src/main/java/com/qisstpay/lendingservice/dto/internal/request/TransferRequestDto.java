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
    private TransferType type;
    private String identityNumber;
    private String phoneNumber;
    private Long bankId;
    private String accountNo;
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
