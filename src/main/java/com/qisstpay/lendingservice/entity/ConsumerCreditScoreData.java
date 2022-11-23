package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "credit_score_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ConsumerCreditScoreData {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Score", columnDefinition = "INT default 1")
    private Integer score;

    @Column(name = "Month")
    private String month;

    @Column(name = "Remarks")
    private String remarks;

    @Column(name = "cnic")
    private String cnic;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
    @JoinColumn(name="consumer_id", referencedColumnName = "id")
    @JsonIgnore
    private Consumer consumer;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
