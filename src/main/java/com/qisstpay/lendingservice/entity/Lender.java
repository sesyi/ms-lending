package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.StatusType;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "lender")
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
    private List<LenderCallLog> lenderCallsHistories;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusType status;
}
