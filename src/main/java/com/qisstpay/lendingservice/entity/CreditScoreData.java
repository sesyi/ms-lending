package com.qisstpay.lendingservice.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "credit_score_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreditScoreData {

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
}
