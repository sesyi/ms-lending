package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qisstpay.lendingservice.enums.ServiceType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "collection_balance_sheet")
public class CollectionBalanceSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
    @JoinColumn(name="lender_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @Column(name = "transaction_id")
    private String collectionTransactionId;

    @Column(name = "disperse_id")
    private String disbursementTransactionStamp;

    @Column(name = "credit")
    private Double credit;

    @Column(name = "debit")
    private Double debit;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "short_description")
    private String shortDescription;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
