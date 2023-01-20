package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.response.GetBanksListResponseDto;
import com.qisstpay.lendingservice.entity.Bank;

public interface BankService {

    GetBanksListResponseDto getBanks();

    Bank getByNIFTCode(String code);
}
