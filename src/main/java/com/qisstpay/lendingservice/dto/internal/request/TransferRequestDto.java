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
    private String cnic;
    private String phoneNumber;
    private String consumerNumber;
    private String bankCode;
    private String accountNumber;
    private String accountNo; //to be removed later on when communicated to the lenders to use accountNumber
    private double amount;

    @Override
    public String toString() {
        return "TransferRequestDto{" +
                "userName='" + userName + '\'' +
                ", identityNumber='" + cnic + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
