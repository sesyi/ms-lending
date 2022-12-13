package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.internal.response.BankResponseDto;
import com.qisstpay.lendingservice.dto.internal.response.GetBanksListResponseDto;
import com.qisstpay.lendingservice.entity.Bank;
import com.qisstpay.lendingservice.error.errortype.BankErrorType;
import com.qisstpay.lendingservice.repository.BankRepository;
import com.qisstpay.lendingservice.service.BankService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    @Override
    public GetBanksListResponseDto getBanks() {
        return GetBanksListResponseDto.builder().banks(
                bankRepository.findAll().stream().filter(bank -> StringUtils.isNotBlank(bank.getCode())).map(bank -> BankResponseDto.builder().name(bank.getName()).code(bank.getCode()).build()).collect(Collectors.toList())
        ).build();
    }

    @Override
    public Bank getByCode(String code) {
        Optional<Bank> bank = bankRepository.findByCode(code);
        if (bank.isPresent()) {
            return bank.get();
        } else {
            log.info(BankErrorType.ENABLE_TO_GET_BANK.getErrorMessage());
            throw new ServiceException(BankErrorType.ENABLE_TO_GET_BANK);
        }
    }
}
