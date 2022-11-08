package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qisstpay.lendingservice.enums.GenderType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

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

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "father_or_husband_name")
    private String fatherOrHusbandName;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Column(name = "cnic")
    private String cnic;

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
}
