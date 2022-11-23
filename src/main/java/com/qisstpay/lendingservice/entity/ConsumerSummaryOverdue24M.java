package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "summary_overdue_24M")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ConsumerSummaryOverdue24M {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plus_30_24M")
    private String plus_30_24M;

    @Column(name = "plus_60_24M")
    private String plus_60_24M;

    @Column(name = "plus_90_24M")
    private String plus_90_24M;

    @Column(name = "plus_120_24M")
    private String plus_120_24M;

    @Column(name = "plus_150_24M")
    private String plus_150_24M;

    @Column(name = "plus_180_24M")
    private String plus_180_24M;

    @Column(name = "mfi_default")
    private String mfi_default;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "consumer_id", referencedColumnName = "id")
    @JsonIgnore
    private Consumer consumer;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
