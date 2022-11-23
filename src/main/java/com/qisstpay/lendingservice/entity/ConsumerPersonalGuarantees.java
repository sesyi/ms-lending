package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "personal_guarantees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ConsumerPersonalGuarantees {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product")
    private String product;

    @Column(name = "principal_borrower_name")
    private String principalBorrowerName;

    @Column(name = "principal_borrower_cnic")
    private String principalBorrowerCnic;

    @Column(name = "date_of_invocation")
    private String dateOfInvocation;

    @Column(name = "guarantee_date")
    private String guaranteeDate;

    @Column(name = "guarantee_amount")
    private String guaranteeAmount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "consumer_id", referencedColumnName = "id")
    @JsonIgnore
    private Consumer consumer;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
