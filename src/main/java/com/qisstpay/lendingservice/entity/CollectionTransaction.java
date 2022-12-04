package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.TransactionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

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
    private double amount;

    @Column(name = "amount_after_due_date")
    private double amountAfterDueDate;

    @Column(name = "due_date")
    private Timestamp dueDate;

    @Column(name = "bill_status")
    private String billStatus;

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

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="lender_call_id")
    private LenderCallLog lenderCall;

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="lender_id")
    private User lender;
}
