package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qisstpay.lendingservice.enums.BillStatusType;
import com.qisstpay.lendingservice.enums.TransactionState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
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

    @Column(name = "consumer_number")
    private String identityNumber;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "amount_after_due_date")
    private Double amountAfterDueDate;

    @Column(name = "amount_within_due_date")
    private Double amountWithinDueDate;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "date_paid")
    private Date datePaid;

    @Column(name = "billing_month")
    private String billingMonth;

    @Column(name = "bill_status")
    private BillStatusType billStatus;

    @Column(name = "consumer_name")
    private String consumerName;

    @Column(name = "transaction_state")
    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;

    @Column(name = "transaction_id")
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lender_call_id")
    private LenderCallLog lenderCall;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ep_call_id")
    private EPCallLog epCallLog;

    @Column(name = "identification_parameter")
    private String identificationParameter;
    @Column(name = "reserved")
    private String reserved;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "collectionTransaction")
    private List<QpayPaymentTransaction> qpayPaymentTransaction;
}
