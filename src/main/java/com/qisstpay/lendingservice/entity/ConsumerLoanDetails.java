package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "loan_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ConsumerLoanDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_serial_number")
    private String loanSerialNumber;

    @Column(name = "product")
    private String product;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "loan_account_status")
    private String loanAccountStatus;

    @Column(name = "loan_last_payment_amount")
    private String loanLastPaymentAmount;

    @Column(name = "loan_id")
    private String loanId;

    @Column(name = "loan_limit")
    private String loanLimit;

    @Column(name = "Loan_type")
    private String LoanType;

    @Column(name = "position_as_of")
    private String positionAsOf;

    @Column(name = "outstanding_balance")
    private String outstandingBalance;

    @Column(name = "date_of_last_payment_name")
    private String dateOfLastPaymentName;

    @Column(name = "repayment_frequency")
    private String repaymentFrequency;

    @Column(name = "minimum_amount_due")
    private String minimumAmountDue;

    @Column(name = "facility_date")
    private String facilityDate;

    @Column(name = "classification_amount")
    private String classificationAmount;

    @Column(name = "collateral_amount")
    private String collateralAmount;

    @Column(name = "maturity_date")
    private String maturityDate;

    @Column(name = "classification_type")
    private String classificationType;

    @Column(name = "litigation_amount")
    private String litigationAmount;

    @Column(name = "bounced_repayment_cheque")
    private String bouncedRepaymentCheque;

    @Column(name = "restructuring_date")
    private String restructuringDate;

    @Column(name = "secured_unsecured")
    private String securedUnsecured;

    @Column(name = "security_collateral")
    private String securityCollateral;

    @Column(name = "restructuring_amount")
    private String restructuringAmount;

    @Column(name = "write_off_type")
    private String writeOffType;

    @Column(name = "write_off_amount")
    private String writeOffAmount;

    @Column(name = "write_off_date")
    private String writeOffDate;

    @Column(name = "recovery_amount")
    private String recoveryAmount;

    @Column(name = "recovery_date")
    private String recoveryDate;

    @Column(name = "plus_30")
    private String pluse30;

    @Column(name = "plus_60")
    private String plus60;

    @Column(name = "plus_90")
    private String plus90;

    @Column(name = "plus_120")
    private String plus120;

    @Column(name = "plus_150")
    private String plus150;

    @Column(name = "plus_180")
    private String plus180;

    @Column(name = "mfi_default")
    private String mfiDefault;

    @Column(name = "late_payment_1TO15")
    private String latePayment1TO15;

    @Column(name = "late_payment_16TO20")
    private String latePayment16TO20;

    @Column(name = "late_payment_21TO29")
    private String latePayment21TO29;

    @Column(name = "late_payment_30")
    private String latePayment30;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "consumer_id", referencedColumnName = "id")
    @JsonIgnore
    private Consumer consumer;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
