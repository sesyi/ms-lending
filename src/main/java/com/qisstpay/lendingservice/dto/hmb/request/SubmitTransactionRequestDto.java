package com.qisstpay.lendingservice.dto.hmb.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitTransactionRequestDto {
    @JsonProperty("ChannelID")
    private String channelID;
    @JsonProperty("ProductCode")
    private String productCode;
    @JsonProperty("DRAccountNo")
    private String drAccountNo;
    @JsonProperty("DRAccTitle")
    private String drAccTitle;
    @JsonProperty("Stan")
    private String stan;
    @JsonProperty("DateTime")
    private String dateTime;
    @JsonProperty("FileTemplate")
    private String fileTemplate;
    @JsonProperty("MakerID")
    private String makerID;
    @JsonProperty("CheckerID")
    private String checkerID;
    @JsonProperty("Signatory1ID")
    private String signatory1ID;
    @JsonProperty("Signatory2ID")
    private String signatory2ID;
    @JsonProperty("Signatory3ID")
    private String signatory3ID;
    @JsonProperty("ReleaserID")
    private String releaserID;
    @JsonProperty("Transactions")
    List<TransactionDto> transactions;
}
