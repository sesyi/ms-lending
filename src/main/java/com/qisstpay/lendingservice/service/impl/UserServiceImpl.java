package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.commons.error.errortype.UserErrorType;
import com.qisstpay.commons.exception.ServiceException;
import com.qisstpay.lendingservice.dto.internal.request.UserRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.entity.User;
import com.qisstpay.lendingservice.enums.StatusType;
import com.qisstpay.lendingservice.repository.UserRepository;
import com.qisstpay.lendingservice.security.ApiKeyAuth;
import com.qisstpay.lendingservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public MessageResponseDto saveUser(UserRequestDto userRequestDto) {
        try {
            User newUser;
            Optional<User> user = userRepository.getByUserId(userRequestDto.getUserId());
            if (user.isPresent()) {
                newUser = user.get();
                newUser.setApiKey(userRequestDto.getApiKey());
                newUser.setCredentialFileUrl(userRequestDto.getCredentialFileUrl());
            } else {
                newUser = User.builder()
                        .apiKey(userRequestDto.getApiKey())
                        .userId(userRequestDto.getUserId())
                        .status(StatusType.PENDING)
                        .userType(userRequestDto.getUserType())
                        .userName(userRequestDto.getUserName())
                        .credentialFileUrl(userRequestDto.getCredentialFileUrl()).build();
            }
            userRepository.save(newUser);
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
        Optional<User> user = userRepository.getByUserId(userId);
        if (user.isPresent()) {
            if (user.get().getStatus().equals(StatusType.BLOCKED)) {
                throw new ServiceException(UserErrorType.LENDER_BLOCKED);
            }
            if (user.get().getStatus().equals(StatusType.PENDING)) {
                user.get().setStatus(StatusType.ACTIVE);
                userRepository.save(user.get());
            } else if (user.get().getStatus().equals(StatusType.BLOCKED)) {
                throw new ServiceException(UserErrorType.USER_BLOCKED);
            }
            Boolean check = ApiKeyAuth.verifyApiKey(apiKey, user.get().getApiKey());
            return MessageResponseDto.builder()
                    .message(check ? "User Verification Success" : "User Verification Failed")
                    .success(check)
                    .build();
        }
        throw new ServiceException(UserErrorType.USER_NOT_FOUND);
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return userRepository.getByUserId(userId);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            if (user.get().getStatus().equals(StatusType.BLOCKED)) {
                log.error(UserErrorType.LENDER_BLOCKED.getErrorMessage());
                throw new ServiceException(UserErrorType.LENDER_BLOCKED);
            }
        } else {
            log.error(UserErrorType.USER_NOT_FOUND.getErrorMessage());
            throw new ServiceException(UserErrorType.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public Optional<User> getUser(String userName) {
        return userRepository.getByUserName(userName);
    }
}
