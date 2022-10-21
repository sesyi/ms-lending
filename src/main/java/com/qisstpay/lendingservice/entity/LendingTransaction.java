package com.qisstpay.lendingservice.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "amount")
    private double amount;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;
    @CreationTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
