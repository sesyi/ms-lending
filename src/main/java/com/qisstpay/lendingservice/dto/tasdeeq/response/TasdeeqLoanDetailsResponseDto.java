package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqLoanDetailsResponseDto {

    @JsonProperty("LOAN_SERIAL_NUMBER")
    private Integer loanSerialNumber;

    @JsonProperty("PRODUCT")
    private String product;

    @JsonProperty("BANK_NAME")
    private String bankName;

    @JsonProperty("LOAN_ACCOUNT_STATUS")
    private String loanAccountStatus;

    @JsonProperty("LOAN_LAST_PAYMENT_AMOUNT")
    private String loanLastPaymentAmount;

    @JsonProperty("LOAN_ID")
    private String loanId;

    @JsonProperty("LOAN_LIMIT")
    private String loanLimit;

    @JsonProperty("LOAN_TYPE")
    private String LoanType;

    @JsonProperty("POSITION_AS_OF")
    private String positionAsOf;

    @JsonProperty("OUTSTANDING_BALANCE")
    private String outstandingBalance;

    @JsonProperty("DATE_OF_LAST_PAYMENT_MADE")
    private String dateOfLastPaymentName;

    @JsonProperty("REPAYMENT_FREQUENCY")
    private String repaymentFrequency;

    @JsonProperty("MINIMUM_AMOUNT_DUE")
    private String minimumAmountDue;

    @JsonProperty("FACILITY_DATE")
    private String facilityDate;

    @JsonProperty("CLASSIFICATION_NATURE")
    private String classificationAmount;

    @JsonProperty("COLLATERAL_AMOUNT")
    private String collateralAmount;

    @JsonProperty("MATURITY_DATE")
    private String maturityDate;

    @JsonProperty("CLASSIFICATION_TYPE")
    private String classificationType;

    @JsonProperty("LITIGATION_AMOUNT")
    private String litigationAmount;

    @JsonProperty("BOUNCED_REPAYMENT_CHEQUES")
    private String bouncedRepaymentCheque;

    @JsonProperty("RESTRUCTURING_DATE")
    private String restructuringDate;

    @JsonProperty("SECURED_UNSECURED")
    private String securedUnsecured;

    @JsonProperty("SECURITY_COLLATERAL")
    private String securityCollateral;

    @JsonProperty("RESTRUCTURING_AMOUNT")
    private String restructuringAmount;

    @JsonProperty("WRITEOFF_TYPE")
    private String writeOffType;

    @JsonProperty("WRITE_OFF_AMOUNT")
    private String writeOffAmount;

    @JsonProperty("WRITEOFF_DATE")
    private String writeOffDate;

    @JsonProperty("RECOVERY_AMOUNT")
    private String recoveryAmount;

    @JsonProperty("RECOVERY_DATE")
    private String recoveryDate;

    @JsonProperty("PLUS_30")
    private String pluse30;

    @JsonProperty("PLUS_60")
    private String plus60;

    @JsonProperty("PLUS_90")
    private String pluse90;

    @JsonProperty("PLUS_120")
    private String plus120;

    @JsonProperty("PLUS_150")
    private String plus150;

    @JsonProperty("PLUS_180")
    private String plus180;

    @JsonProperty("MFI_DEFAULT")
    private String mfiDefault;

    @JsonProperty("LATE_PAYMENT_1TO15")
    private String latePayment1TO15;

    @JsonProperty("LATE_PAYMENT_16TO20")
    private String latePayment16TO20;

    @JsonProperty("LATE_PAYMENT_21TO29")
    private String latePayment21TO29;

    @JsonProperty("LATE_PAYMENT_30")
    private String latePayment30;
}