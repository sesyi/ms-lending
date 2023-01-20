package com.qisstpay.lendingservice.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "banks")
public class Bank {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "hmb_code")
    private String hmbCode;

    @Column(name = "nift_code")
    private String niftCode;

    @Column(name = "country_id")
    private Long countryId;
}
