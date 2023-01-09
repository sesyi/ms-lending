package com.qisstpay.lendingservice.dto.internal.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qisstpay.lendingservice.enums.TransferType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferRequestDto {

    private String userName;
    private TransferType type;
    private String cnic;
    private String phoneNumber;
    private String consumerNumber;
    private String identityNumber; //TODO: to be removed later on when communicated to the lenders to use accountNumber
    private String bankCode;
    private String accountTitle;
    private String accountNumber;
    private String accountNo; //TODO: to be removed later on when communicated to the lenders to use accountNumber
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
