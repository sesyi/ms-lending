package com.qisstpay.lendingservice.dto.internal.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qisstpay.lendingservice.enums.ServiceType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ConfigRequestDto {
    private ServiceType serviceType;
    private Double      charge;
    private String      description;
}
