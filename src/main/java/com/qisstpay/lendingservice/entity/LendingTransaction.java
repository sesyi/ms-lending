package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.TransactionState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "lending_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendingTransaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "identity_number")
    private String identityNumber;
    @Column(name = "amount")
    private double amount;
    @Column(name = "ep_transaction_id")
    private String epTransactionId;
    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;
    @Column(name = "transaction_state")
    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;
    @Column(name = "ep_inquiry_response")
    private String epInquiryResponse;
    @Column(name = "ep_transfer_response")
    private String epTransferResponse;
}
