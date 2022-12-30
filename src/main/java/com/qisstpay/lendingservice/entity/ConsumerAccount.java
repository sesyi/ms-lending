package com.qisstpay.lendingservice.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consumer_account")
public class ConsumerAccount {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_title")
    private String accountTitle;

    @Column(name = "accountNumber")
    private String accountNumber;

    @Column(name = "iban_number")
    private String ibanNumber;

    @Column(name = "verified")
    private Boolean verifiedCheck;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", referencedColumnName = "id")
    private Bank bank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id", referencedColumnName = "id")
    private Consumer consumer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumerAccount")
    private List<QpayPaymentTransaction> qpayPaymentTransaction;
}
