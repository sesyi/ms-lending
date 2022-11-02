package com.qisstpay.lendingservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "consumer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consumer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone_number")
    private String phoneNumber;
    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
