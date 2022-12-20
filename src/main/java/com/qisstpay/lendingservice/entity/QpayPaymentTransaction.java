package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.BillStatusType;
import com.qisstpay.lendingservice.enums.PaymentGatewayType;
import com.qisstpay.lendingservice.enums.TransactionState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "qpay_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QpayPaymentTransaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authorized_payment")
    private String authorizedPayment;

    @Column(name = "gateway_customer_id")
    private String gatewayCustomerID;

    @Column(name = "gateway_response_id")
    private String gateway_response_id;

    @Column(name = "gateway")
    @Enumerated(EnumType.STRING)
    private PaymentGatewayType gateway;

    @Column(name = "transaction_id")
    private String serviceTransactionId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_transaction_id")
    private CollectionTransaction collectionTransaction;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consumer_account_id")
    private ConsumerAccount consumerAccount;
}