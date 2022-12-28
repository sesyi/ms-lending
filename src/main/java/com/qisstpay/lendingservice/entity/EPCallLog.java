package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.CallType;
import com.qisstpay.lendingservice.enums.EndPointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "ep_calls_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EPCallLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CallStatusType status;

    @Column(name = "end_point")
    @Enumerated(EnumType.STRING)
    private EndPointType endPoint;

    @Column(name = "call_type")
    @Enumerated(EnumType.STRING)
    private CallType callType;

    @Column(name ="statusCode")
    private String statusCode;

    @Column(name ="message")
    private String message;

    @CreationTimestamp
    @Column(name = "requested_at")
    private Timestamp requestedAt;

    @UpdateTimestamp
    @Column(name = "response_at")
    private Timestamp responseAt;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="lending_trxn_id")
    private LendingTransaction lendingTransaction;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="collection_trxn_id")
    private CollectionTransaction collectionTransaction;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "mfb_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
}
