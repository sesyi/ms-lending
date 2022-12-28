package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.ServiceType;
import com.qisstpay.lendingservice.enums.TransactionState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "lending_transactions")
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

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "amount")
    private double amount;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "ep_transaction_id")
    private String serviceTransactionId;

    @Column(name = "transaction_stamp")
    private String transactionStamp;

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

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="lender_call_id")
    private LenderCallLog lenderCall;
}
