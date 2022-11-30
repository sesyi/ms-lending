package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.EndPointType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tasdeeq_calls_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TasdeeqCallLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request")
    private String request;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CallStatusType status;

    @Column(name = "end_point")
    @Enumerated(EnumType.STRING)
    private EndPointType endPoint;

    @Column(name = "statusCode")
    private String statusCode;

    @Column(name = "messageCode")
    private String messageCode;

    @Column(name = "message")
    private String message;

    @CreationTimestamp
    @Column(name = "requested_at")
    private Timestamp requestedAt;

    @UpdateTimestamp
    @Column(name = "response_at")
    private Timestamp responseAt;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "lender_call_id", referencedColumnName = "id")
    @JsonIgnore
    private LenderCallLog lenderCall;
}
