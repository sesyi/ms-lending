package com.qisstpay.lendingservice.dto.tasdeeq.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasdeeqConsumerReportResponseDto {

    @JsonProperty("reportDate")
    private String reportDate;

    @JsonProperty("reportTime")
    private String reportTime;

    @JsonProperty("refNo")
    private String refNo;

    @JsonProperty("noOfCreditEnquiry")
    private Integer noOfCreditEnquiry;

    @JsonProperty("noOfActiveAccounts")
    private Integer noOfActiveAccounts;

    @JsonProperty("totalOutstandingBalance")
    private String totalOutstandingBalance;

    @JsonProperty("disclaimerText")
    private String disclaimerText;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("personalInformation")
    private TasdeeqConsumerPersonalInformationResponseDto personalInformation;

    @JsonProperty("creditScoreData")
    private TasdeeqCreditScoreDataResponseDto creditScoreData;

    @JsonProperty("summaryOverdue_24M")
    private List<TasdeeqSummaryOverdue24MResponseDto> summaryOverdue_24M;

    @JsonProperty("detailsOfStatusCreditApplication")
    private List<TasdeeqDetailsOfStatusCreditApplicationResponseDto> detailsOfStatusCreditApplication;

    @JsonProperty("detailsOfLoansSettlement")
    private List<TasdeeqDetailsOfLoansSettlementResponseDto> detailsOfLoansSettlement;

    @JsonProperty("personalGuarantees")
    private List<TasdeeqPersonalGuaranteesResponseDto> personalGuarantees;

    @JsonProperty("coborrowerDetail")
    private List<TasdeeqCoborrowerDetailResponseDto> coborrowerDetail;

    @JsonProperty("detailsOfBankruptcyCases")
    private List<TasdeeqDetailsOfBankruptcyCasesResponseDto> detailsOfBankruptcyCases;

    @JsonProperty("creditEnquiry")
    private List<TasdeeqCreditEnquiryResponseDto> creditEnquiry;

    @JsonProperty("loanDetails")
    private List<TasdeeqLoanDetailsResponseDto> loanDetails;

    @JsonProperty("creditHistory")
    private List<TasdeeqCreditHistoryResponseDto> creditHistory;
}
