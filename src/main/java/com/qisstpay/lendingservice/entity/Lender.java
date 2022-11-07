package com.qisstpay.lendingservice.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "lenders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Lender {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "credential_file_url")
    private String credentialFileUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lender")
    private List<LenderCallsHistory> lenderCallsHistories;
}
