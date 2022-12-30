package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.PaymentGatewayType;
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

    @Column(name = "amount")
    private Double amount;

    @Column(name = "further_action")
    private Boolean furtherAction;

    @Column(name = "authorized_payment")
    private Boolean authorizedPayment;

    @Column(name = "gateway_card_source_id")
    private String gatewayCardSourceId;

    @Column(name = "gateway_client_secret")
    private String gatewayClientSecret;

    @Column(name = "gateway_code")
    private String gatewayCode;

    @Column(name = "gateway_message")
    private String gatewayMessage;

    @Column(name = "gateway_source")
    private String gatewaySource;

    @Column(name = "gateway_status")
    private String gatewayStatus;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "htmlSnippet")
    private String htmlSnippet;

    @Column(name = "redirect_url")
    private String redirectURL;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "ref_transaction_id")
    private String refTransactionId;

    @Column(name = "gateway")
    @Enumerated(EnumType.STRING)
    private PaymentGatewayType gateway;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_transaction_id")
    private CollectionTransaction collectionTransaction;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consumer_account_id")
    private ConsumerAccount consumerAccount;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}