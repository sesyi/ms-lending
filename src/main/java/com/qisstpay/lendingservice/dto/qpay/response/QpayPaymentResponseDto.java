
package com.qisstpay.lendingservice.dto.qpay.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QpayPaymentResponseDto {
    @JsonProperty("further_action")
    private Boolean furtherAction;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("service_message")
    private String serviceMessage;

    @JsonProperty("redirect_url")
    private String redirectURL;

    @JsonProperty("html_snippet")
    private String htmlSnippet;

    @JsonProperty("gateway_response")
    private GatewayResponseDto gatewayResponse;
}
