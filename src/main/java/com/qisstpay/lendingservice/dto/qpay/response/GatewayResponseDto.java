
package com.qisstpay.lendingservice.dto.qpay.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GatewayResponseDto {
    @JsonProperty("authorized_payment")
    private Boolean authorizedPayment;

    @JsonProperty("gateway")
    private String gatewayType;

    @JsonProperty("gateway_code")
    private String gatewayCode;

    @JsonProperty("gateway_message")
    private String gatewayMessage;

    @JsonProperty("gateway_response_id")
    private String gatewayResponseId;

    @JsonProperty("gateway_status")
    private String gatewayStatus;

    @JsonProperty("gateway_card_source_id")
    private String gatewayCardSourceId;

    @JsonProperty("gateway_client_secret")
    private String gatewayClientSecret;

    @JsonProperty("gateway_customer_id")
    private String gatewayCustomerId;

    @JsonProperty("gateway_source")
    private String gatewaySource;

    @JsonProperty("payment_status")
    private String paymentStatus;
}