package com.qisstpay.lendingservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qisstpay.lendingservice.dto.hmb.request.InvoiceDto;
import com.qisstpay.lendingservice.dto.hmb.request.SubmitTransactionRequestDto;
import com.qisstpay.lendingservice.dto.hmb.request.TransactionDto;
import com.qisstpay.lendingservice.dto.internal.request.CreditScoreRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.request.TasdeeqReportDataRequestDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqAuthResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerPersonalInformationResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqCreditScoreDataResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.entity.ConsumerCreditScoreData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;

@Component
@RequiredArgsConstructor
public class ModelConverter {
    private final ModelMapper  modelMapper;
    private final ObjectMapper objectMapper;


    public Consumer convertToConsumer(TasdeeqConsumerPersonalInformationResponseDto consumerInfo) {
        Consumer consumer = modelMapper.map(consumerInfo, Consumer.class);
        consumer.setConsumerCreditScoreData(new ArrayList<>());
        consumer.setSummaryOverdue24Ms(new ArrayList<>());
        consumer.setDetailsOfStatusCreditApplications(new ArrayList<>());
        consumer.setDetailsOfLoansSettlements(new ArrayList<>());
        consumer.setPersonalGuarantees(new ArrayList<>());
        consumer.setCoborrowerDetails(new ArrayList<>());
        consumer.setDetailsOfBankruptcyCases(new ArrayList<>());
        consumer.setCreditEnquiries(new ArrayList<>());
        consumer.setLoanDetails(new ArrayList<>());
        consumer.setCreditHistories(new ArrayList<>());
        return consumer;
    }

    public TasdeeqAuthResponseDto convertTOTasdeeqAuthResponseDto(Object object) {
        return modelMapper.map(object, TasdeeqAuthResponseDto.class);
    }


    public TasdeeqConsumerReportResponseDto convertTOTasdeeqConsumerReportResponseDto(Object object) throws JsonProcessingException {
        return modelMapper.map(object, TasdeeqConsumerReportResponseDto.class);
    }

    public ConsumerCreditScoreData convertToCreditScoreData(TasdeeqCreditScoreDataResponseDto creditScoreDataResponseDto) {
        return modelMapper.map(creditScoreDataResponseDto, ConsumerCreditScoreData.class);
    }

    public TasdeeqReportDataRequestDto convertToTasdeeqReportDataRequestDto(CreditScoreRequestDto creditScoreRequestDto) {
        return modelMapper.map(creditScoreRequestDto, TasdeeqReportDataRequestDto.class);
    }

    public SubmitTransactionRequestDto convertToSubmitTransactionRequestDtoIBFT(String accountNo, String transactionNo, double amount){

        InvoiceDto invoiceDto = InvoiceDto.builder().
                DOCNO("")
                .DOCDESCR("")
                .DOCAMOUNT("")
                .DEDUCTAMOUNT("")
                .NETAMOUNT("")
                .IREF1("").IREF2("").IREF3("").IREF4("").IREF5("")
                .IREF6("").IREF7("").IREF8("").IREF9("").IREF10("")
                .IREF11("").IREF12("").IREF13("").IREF14("").IREF15("")
                .COLUMNORDER("")
                .VALUE_DATE("").build();

        TransactionDto transactionDto = TransactionDto.builder()
                .TXNREFNO(transactionNo)
                .XPIN("")
                .BENEFNAME("Bene Acc Name")
                .BENEMNAME("")
                .BENELNAME("")
                .BENEADDR("")
                .BENECELL("")
                .BENEEMAIL("")
                .BENEIN("").
                BeneAccTitle("Ben Acc Title")
                .BENEACNO("ACC No")
                .SwiftBankCode("")
                .BANK("MDL")
                .BRANCH("")
                .INSTRUMENTNO("")
                .INSTRUMENTPrintDT("")
                .INSTRUMENTDT("")
                .COVERAMOUNT("")
                .CURRENCYCODE("")
                .EXCHANGERATE("")
                .TRANSACTIONAMOUNT(String.valueOf(amount))
                .ADVISING("")
                .PRINTLOC("")
                .REF1("").REF2("").REF3("").REF4("").REF5("")
                .REF6("").REF7("").REF8("").REF9("").REF10("")
                .REF11("").REF12("").REF13("").REF14("").REF15("")
                .REF16("").REF17("").REF18("").REF19("").REF20("")
                .invoices(new ArrayList<>(){{add(invoiceDto);}})
                .build();


        return SubmitTransactionRequestDto.builder()
                .channelID("CMS")
                .productCode("IBFT")
                .drAccountNo(accountNo)
                .drAccTitle("TEST ACCOUNT EFOOD - 6996429311714235925")
                .dateTime("20220523143445")
                .stan(transactionNo)
                .fileTemplate("IBFTE")
                .makerID("EFMAK")
                .releaserID("EFRL")
                .checkerID("EFCHK")
                .signatory1ID("EFSIG")
                .signatory2ID("")
                .signatory3ID("")
                .transactions(new LinkedList<TransactionDto>(){{add(transactionDto);}})
                .build();
    }
}
