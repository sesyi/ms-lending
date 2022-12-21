package com.qisstpay.lendingservice.dto.hmb.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvoiceDto {
    
    @JsonProperty("DOCNO") 
    private String DOCNO;
    @JsonProperty("DOCDT") 
    private String DOCDT;
    @JsonProperty("DOCDESCR") 
    private String DOCDESCR;
    @JsonProperty("DOCAMOUNT")
    private String DOCAMOUNT;
    @JsonProperty("DEDUCTAMOUNT") 
    private String DEDUCTAMOUNT;
    @JsonProperty("NETAMOUNT") 
    private String NETAMOUNT;
    @JsonProperty("IREF1") 
    private String IREF1;
    @JsonProperty("IREF2") 
    private String IREF2;
    @JsonProperty("IREF3")
    private String IREF3;
    @JsonProperty("IREF4") 
    private String IREF4;
    @JsonProperty("IREF5") 
    private String IREF5;
    @JsonProperty("IREF6") 
    private String IREF6;
    @JsonProperty("IREF7") 
    private String IREF7;
    @JsonProperty("IREF8") 
    private String IREF8;
    @JsonProperty("IREF9") 
    private String IREF9;
    @JsonProperty("IREF10") 
    private String IREF10;
    @JsonProperty("IREF11") 
    private String IREF11;
    @JsonProperty("IREF12") 
    private String IREF12;
    @JsonProperty("IREF13") 
    private String IREF13;
    @JsonProperty("IREF14") 
    private String IREF14;
    @JsonProperty("IREF15") 
    private String IREF15;
    @JsonProperty("COLUMNORDER") 
    private String COLUMNORDER;
    @JsonProperty("VALUE_DATE")
    private String VALUE_DATE;


}

