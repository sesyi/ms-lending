package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.internal.response.BankResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.GetBanksListResponseDto;
import com.qisstpay.lendingservice.entity.Bank;
import com.qisstpay.lendingservice.repository.BankRepository;
import com.qisstpay.lendingservice.service.BankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    @Override
    public GetBanksListResponseDto getBanks() {
        return GetBanksListResponseDto.builder().banks(
                bankRepository.findAll().stream().map(bank -> BankResponseDto.builder().id(bank.getId()).code(bank.getCode()).build()).collect(Collectors.toList())
        ).build();
    }
}
