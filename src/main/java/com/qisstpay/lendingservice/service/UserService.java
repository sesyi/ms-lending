package com.qisstpay.lendingservice.service;

import com.qisstpay.lendingservice.dto.internal.request.UserRequestDto;
import com.qisstpay.lendingservice.dto.internal.response.MessageResponseDto;
import com.qisstpay.lendingservice.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    MessageResponseDto saveUser(UserRequestDto userRequestDto);

    MessageResponseDto verifyUser(Long userId, String apiKey);

    Optional<User> getUserByUsername(Long userId);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String userName);

    Optional<User> getUserByUcid(String ucid);
}

