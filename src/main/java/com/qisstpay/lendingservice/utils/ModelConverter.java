package com.qisstpay.lendingservice.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ModelConverter {
    private final ModelMapper modelMapper;

//    public Category convertToCategoryEntity(CategoryDto categoryDto) {
//        return modelMapper.map(categoryDto, Category.class);
//    }

}
