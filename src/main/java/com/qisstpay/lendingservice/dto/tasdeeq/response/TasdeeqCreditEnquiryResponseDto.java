package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqCreditEnquiryResponseDto {

    @JsonProperty("SR_NO")
    private String sr_no;

    @JsonProperty("FI_TYPE")
    private String fi_type;

    @JsonProperty("DATE_OF_ENQUIRY")
    private String date_of_enquiry;

}
