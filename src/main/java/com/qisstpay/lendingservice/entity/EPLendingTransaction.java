package com.qisstpay.lendingservice.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ep_lending_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EPLendingTransaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
