package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.response.GetBanksListResponseDto;
import com.qisstpay.lendingservice.entity.Bank;
import com.qisstpay.lendingservice.entity.CollectionBalanceSheet;
import com.qisstpay.lendingservice.entity.CollectionTransaction;

import java.util.List;

public interface CollectionBalanceSheetService {

    void createBalanceEntryAndSave(CollectionTransaction collectionTransaction);

    void save (List<CollectionBalanceSheet> collectionBalanceSheets);
}
