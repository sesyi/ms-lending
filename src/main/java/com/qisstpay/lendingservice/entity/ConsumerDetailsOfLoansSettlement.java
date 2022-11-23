package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "details_of_loans_settlement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ConsumerDetailsOfLoansSettlement {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product")
    private String product;

    @Column(name = "total_limit")
    private String totalLimit;

    @Column(name = "approval_date")
    private String approvalDate;

    @Column(name = "relationship_date")
    private String relationshipDate;

    @Column(name = "maturity_date")
    private String maturityDate;

    @Column(name = "date_of_settlement")
    private String dateOfSettlement;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "consumer_id", referencedColumnName = "id")
    @JsonIgnore
    private Consumer consumer;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
