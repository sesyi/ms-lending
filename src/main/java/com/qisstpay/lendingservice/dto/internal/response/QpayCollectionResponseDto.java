
package com.qisstpay.lendingservice.dto.internal.response;

import com.qisstpay.lendingservice.enums.BillStatusType;
import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QpayCollectionResponseDto {
    private Long               billId;
    private BillStatusType     billStatus;
    private Boolean            furtherAction;
    private String             redirectURL;
    private String             htmlSnippet;
    private Boolean            authorizedPayment;
    private String             transactionId;
    private String             status;
    private String             source;
    private String             message;
    private String             paymentStatus;
    private PaymentGatewayType gateway;
}
