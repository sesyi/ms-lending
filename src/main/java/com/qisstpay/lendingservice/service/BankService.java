package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.response.BankResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.GetBanksListResponseDto;
import com.qisstpay.lendingservice.entity.Bank;

import java.util.List;

public interface BankService {

    GetBanksListResponseDto getBanks();
}
