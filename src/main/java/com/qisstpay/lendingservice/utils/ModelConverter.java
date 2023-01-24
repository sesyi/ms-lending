package com.qisstpay.lendingservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qisstpay.lendingservice.dto.hmb.HMBCredentials;
import com.qisstpay.lendingservice.dto.hmb.request.*;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

@Component
@RefreshScope
@RequiredArgsConstructor
public class ModelConverter {

    private final ModelMapper  modelMapper;
    private final ObjectMapper objectMapper;


    @Value("${config.hmb.makerid}")
    private String hmbMakerId;

    @Value("${config.hmb.checkerid}")
    private String hmbCheckerId;

    @Value("${config.hmb.signatoryid}")
    private String hmbSignatoryId;

    @Value("${config.hmb.releaserid}")
    private String hmbReleaserId;

    @Value("${config.hmb.donoraccountno}")
    private String donorAccountNumber;

    @Value("${config.hmb.donoraccounttitle}")
    private String donorAccountTitle;


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

    public SubmitIBFTTransactionRequestDto convertToSubmitTransactionRequestDtoIBFT(HMBCredentials hmbCredentials, String benAccountBankCode,  String benAccountTitle, String benAccountNo, String transactionNo, String stan, double amount) {

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
                .BENEFNAME(benAccountTitle)
                .BENEMNAME("")
                .BENELNAME("")
                .BENEADDR("")
                .BENECELL("")
                .BENEEMAIL("")
                .BENEIN("")
                .BeneAccTitle(benAccountTitle)
                .BENEACNO(benAccountNo)
                .SwiftBankCode("")
                .BANK(benAccountBankCode)
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
                .REF6("").REF7("").REF8("").REF9("Loan").REF10("")
                .REF11("").REF12("").REF13("").REF14("").REF15("")
                .REF16("").REF17("").REF18("").REF19("").REF20("")
                .invoices(new ArrayList<>() {{
                    add(invoiceDto);
                }})
                .build();


        return SubmitIBFTTransactionRequestDto.builder()
                .channelID("CMS")
                .productCode("IBFT")
                .drAccountNo(hmbCredentials.getAccountNumber())
                .drAccTitle(hmbCredentials.getAccountTitle())
                .dateTime(getHMBFormatDate())
                .stan(stan)
                .fileTemplate("IBFTE")
                .makerID(hmbMakerId)
                .releaserID(hmbReleaserId)
                .checkerID(hmbCheckerId)
                .signatory1ID(hmbSignatoryId)
                .signatory2ID("")
                .signatory3ID("")
                .transactions(new LinkedList<TransactionDto>() {{
                    add(transactionDto);
                }})
                .build();
    }

    public SubmitIFTTransactionRequestDto convertToSubmitTransactionRequestDtoIFT(HMBCredentials hmbCredentials, String benAccountBankCode,  String benAccountTitle, String benAccountNo, String transactionNo, String stan, double amount) {

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
                .BENEFNAME(benAccountTitle)
                .BENEMNAME("")
                .BENELNAME("")
                .BENEADDR("")
                .BENECELL("")
                .BENEEMAIL("")
                .BENEIN("")
                .BeneAccTitle(benAccountTitle)
                .BENEACNO(benAccountNo)
                .SwiftBankCode("")
                .BANK(benAccountBankCode)
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
                .invoices(new ArrayList<>() {{
                    add(invoiceDto);
                }})
                .build();


        return SubmitIFTTransactionRequestDto.builder()
                .channelID("CMS")
                .productCode("IFT")
                .drAccountNo(hmbCredentials.getAccountNumber())
                .drAccTitle(hmbCredentials.getAccountTitle())
                .dateTime(getHMBFormatDate())
                .stan(stan)
                .fileTemplate("IFT")
                .makerID(hmbMakerId)
                .releaserID(hmbReleaserId)
                .checkerID(hmbCheckerId)
                .signatory1ID(hmbSignatoryId)
                .signatory2ID("")
                .signatory3ID("")
                .transactions(new LinkedList<TransactionDto>() {{
                    add(transactionDto);
                }})
                .build();
    }

    public GetTransactionStatusRequestDto convertToGetTransactionStatusRequestDto(String stan, String transactionNo) {

        return GetTransactionStatusRequestDto.builder()
                .stan(stan)
                .makerID(hmbMakerId)
                .refNo(transactionNo)
                .dateTime(getHMBFormatDate())
                .build();
    }

    public HMBFetchAccountTitleRequestDto convertToHMBFetchAccountTitleRequestDto(HMBCredentials hmbCredentials, String productCode, String benAccountBankCode, String benAccountNo, String stan){

        TransactionDto transactionDto = TransactionDto.builder()
                .BENEACNO(benAccountNo)
                .BANK(benAccountBankCode)
                .build();

        return HMBFetchAccountTitleRequestDto.builder()
                .channelID("CMS")
                .productCode(productCode)
                .drAccountNo(hmbCredentials.getAccountNumber())
                .drAccTitle(hmbCredentials.getAccountTitle())
//                .dateTime("20220523143445")
                .dateTime(getHMBFormatDate())
                .stan(stan)
                .fileTemplate("IFT")
                .makerID(hmbMakerId)
                .releaserID(hmbReleaserId)
                .checkerID(hmbCheckerId)
                .signatory1ID(hmbSignatoryId)
                .signatory2ID("")
                .signatory3ID("")
                .transactions(new LinkedList<TransactionDto>() {{
                    add(transactionDto);
                }})
                .build();
    }

    private String getHMBFormatDate(){
        String dateInString = (LocalDateTime.now().atZone(ZoneId.of("GMT+05:00")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        return dateInString;
    }
}
