package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.BillStatusType;
import com.qisstpay.lendingservice.enums.TransactionState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "collection_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionTransaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "identity_number")
    private String identityNumber;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "amount_after_due_date")
    private Double amountAfterDueDate;

    @Column(name = "due_date")
    private Timestamp dueDate;

    @Column(name = "bill_status")
    private BillStatusType billStatus;

    @Column(name = "transaction_state")
    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;

    @Column(name = "transaction_id")
    private String serviceTransactionId;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lender_call_id")
    private LenderCallLog lenderCall;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "collectionTransaction")
    private List<QpayPaymentTransaction> qpayPaymentTransaction;
}
