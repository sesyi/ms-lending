package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.response.GetBanksListResponseDto;
import com.qisstpay.lendingservice.entity.Bank;
import com.qisstpay.lendingservice.entity.CollectionTransaction;

public interface CollectionBalanceSheetService {

    void save(CollectionTransaction collectionTransaction);
}
