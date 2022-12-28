package com.qisstpay.lendingservice.dto.internal.request;

import com.qisstpay.lendingservice.enums.TransferType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransferRequestDto {

    private String userName;
    private TransferType type;
    private String identityNumber;
    private String phoneNumber;
    private String bankCode;
    private String accountNumber;
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
