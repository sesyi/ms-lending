package com.qisstpay.lendingservice.dto.internal.response;


import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetBanksListResponseDto {
    List<BankResponseDto> banks;
}
