package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.easypaisa.request.EPCollectionInquiryRequest;
import com.qisstpay.lendingservice.dto.easypaisa.response.EPCollectionInquiryResponse;
import org.springframework.stereotype.Service;

@Service
public interface CollectionService {
    EPCollectionInquiryResponse inquiry(EPCollectionInquiryRequest epCollectionInquiryRequest);
}
