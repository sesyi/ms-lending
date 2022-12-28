package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.GenderType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

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

    @Column(name = "cnic")
    private String cnic;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "consumer_number")
    private String consumerNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "father_or_husband_name")
    private String fatherOrHusbandName;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Column(name = "passport")
    private String passport;

    @Column(name = "date_0f_birth")
    private String dateOfBirth;

    @Column(name = "nic")
    private String nic;

    @Column(name = "business_or_profession")
    private String businessOrProfession;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "ntn")
    private String ntn;

    @Column(name = "borrower_type")
    private String borrowerType;

    @Column(name = "current_residential_address")
    private String currentResidentialAddress;

    @Column(name = "current_residential_address_date")
    private String currentResidentialAddressDate;

    @Column(name = "permanent_address")
    private String permanentAddress;

    @Column(name = "permanent_address_date")
    private String permanentAddressDate;

    @Column(name = "previous_residential_address")
    private String previousResidentialAddress;

    @Column(name = "previous_residential_address_date")
    private String previousResidentialAddressDate;

    @Column(name = "employer_or_business")
    private String employerOrBusiness;

    @Column(name = "employer_or_business_date")
    private String employerOrBusinessDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerCreditScoreData> consumerCreditScoreData;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerSummaryOverdue24M> summaryOverdue24Ms;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerDetailsOfStatusCreditApplication> detailsOfStatusCreditApplications;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerDetailsOfLoansSettlement> detailsOfLoansSettlements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerPersonalGuarantees> personalGuarantees;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerCoborrowerDetail> coborrowerDetails;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerDetailsOfBankruptcyCases> detailsOfBankruptcyCases;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerCreditEnquiry> creditEnquiries;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerLoanDetails> loanDetails;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerCreditHistory> creditHistories;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "consumer")
    private List<ConsumerAccount> consumerAccounts;
}
