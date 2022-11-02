package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.error.errortype.UserErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.internal.request.LenderUserRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.entity.Lender;
import com.qisstpay.lendingservice.repository.LenderRepository;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.LenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class LenderServiceImpl implements LenderService {

    @Autowired
    private LenderRepository lenderRepository;

    @Override
    public MessageResponseDto saveLender(LenderUserRequestDto lenderUserRequestDto) {
        try {
            Lender newLender;
            Optional<Lender> lender = lenderRepository.getByUserId(lenderUserRequestDto.getUserId());
            if (lender.isPresent()) {
                newLender = lender.get();
                newLender.setApiKey(lenderUserRequestDto.getApiKey());
                newLender.setCredentialFileUrl(lenderUserRequestDto.getCredentialFileUrl());
            } else {
                newLender = Lender.builder()
                        .apiKey(lenderUserRequestDto.getApiKey())
                        .userId(lenderUserRequestDto.getUserId())
                        .credentialFileUrl(lenderUserRequestDto.getCredentialFileUrl()).build();
            }
            lenderRepository.save(newLender);
            return MessageResponseDto.builder()
                    .message("Lender saved successfully")
                    .success(Boolean.TRUE)
                    .build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return MessageResponseDto.builder()
                    .message("Something went wrong")
                    .success(Boolean.FALSE)
                    .build();
        }
    }

    @Override
    public MessageResponseDto verifyUser(Long userId, String apiKey) {
        Optional<Lender> lender = lenderRepository.getByUserId(userId);
        if (lender.isPresent()) {
            Boolean check = ApiKeyAuth.verifyApiKey(apiKey, lender.get().getApiKey());
            return MessageResponseDto.builder()
                    .message(check ? "User Verification Success" : "User Verification Failed")
                    .success(check)
                    .build();
        }
        throw new ServiceException(UserErrorType.USER_NOT_FOUND);
    }
}
