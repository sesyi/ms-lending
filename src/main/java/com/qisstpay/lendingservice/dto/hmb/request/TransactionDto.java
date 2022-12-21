package com.qisstpay.lendingservice.dto.hmb.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDto {

    @JsonProperty("TXNREFNO")
    private String TXNREFNO;
    @JsonProperty("XPIN")
    private String XPIN;
    @JsonProperty("BENEFNAME")
    private String BENEFNAME;
    @JsonProperty("BENEMNAME")
    private String BENEMNAME;
    @JsonProperty("BENELNAME")
    private String BENELNAME;
    @JsonProperty("BENEADDR")
    private String BENEADDR;
    @JsonProperty("BENECELL")
    private String BENECELL;
    @JsonProperty("BENEEMAIL")
    private String BENEEMAIL;
    @JsonProperty("BENEIN")
    private String BENEIN;
    @JsonProperty("BeneAccTitle")
    private String BeneAccTitle;
    @JsonProperty("BENEACNO")
    private String BENEACNO;
    @JsonProperty("SwiftBankCode")
    private String SwiftBankCode;
    @JsonProperty("BANK")
    private String BANK;
    @JsonProperty("BRANCH")
    private String BRANCH;
    @JsonProperty("INSTRUMENTNO")
    private String INSTRUMENTNO;
    @JsonProperty("INSTRUMENTPrintDT")
    private String INSTRUMENTPrintDT;
    @JsonProperty("INSTRUMENTDT")
    private String INSTRUMENTDT;
    @JsonProperty("COVERAMOUNT")
    private String COVERAMOUNT;
    @JsonProperty("CURRENCYCODE")
    private String CURRENCYCODE;
    @JsonProperty("EXCHANGERATE")
    private String EXCHANGERATE;
    @JsonProperty("TRANSACTIONAMOUNT")
    private String TRANSACTIONAMOUNT;
    @JsonProperty("ADVISING")
    private String ADVISING;
    @JsonProperty("PRINTLOC")
    private String PRINTLOC;
    @JsonProperty("REF1")
    private String REF1;
    @JsonProperty("REF2")
    private String REF2;
    @JsonProperty("REF3")
    private String REF3;
    @JsonProperty("REF4")
    private String REF4;
    @JsonProperty("REF5")
    private String REF5;
    @JsonProperty("REF6")
    private String REF6;
    @JsonProperty("REF7")
    private String REF7;
    @JsonProperty("REF8")
    private String REF8;
    @JsonProperty("REF9")
    private String REF9;
    @JsonProperty("REF10")
    private String REF10;
    @JsonProperty("REF11")
    private String REF11;
    @JsonProperty("REF12")
    private String REF12;
    @JsonProperty("REF13")
    private String REF13;
    @JsonProperty("REF14")
    private String REF14;
    @JsonProperty("REF15")
    private String REF15;
    @JsonProperty("REF16")
    private String REF16;
    @JsonProperty("REF17")
    private String REF17;
    @JsonProperty("REF18")
    private String REF18;
    @JsonProperty("REF19")
    private String REF19;
    @JsonProperty("REF20")
    private String REF20;
    @JsonProperty("Invoice")
    private List<InvoiceDto> invoices;



}
