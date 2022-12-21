package com.qisstpay.lendingservice.dto.internal.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponseDto {

    String transactionId;

    String code;
    String state;
    String description;


}
