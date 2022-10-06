package com.qisstpay.lendingservice.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user_ep_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserEPProfile {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "identity_number")
    private String identityNumber;
    @Column(name = "phone_number")
    private String phoneNumber;
}
