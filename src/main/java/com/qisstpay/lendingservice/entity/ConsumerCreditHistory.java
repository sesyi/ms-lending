package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "credit_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ConsumerCreditHistory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_serial_number")
    private String loan_serial_number;

    @Column(name = "month_name")
    private String monthName;

    @Column(name = "plus_30")
    private String plus30;

    @Column(name = "plus_60")
    private String plus60;

    @Column(name = "plus_90")
    private String plus90;

    @Column(name = "plus_190")
    private String plus120;

    @Column(name = "plus_150")
    private String plus150;

    @Column(name = "plus_180")
    private String plus180;

    @Column(name = "mfi_default")
    private String mfiDefault;

    @Column(name = "late_pmt_days")
    private String latePmtDays;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "consumer_id", referencedColumnName = "id")
    @JsonIgnore
    private Consumer consumer;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
