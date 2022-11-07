package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.ServiceType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "lender_calls_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LenderCallsHistory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
    @JoinColumn(name="lender_id", referencedColumnName = "id")
    @JsonIgnore
    private Lender lender;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CallStatusType status;

    @Column(name = "request")
    private String request;

    @Column(name = "error")
    private String error;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="tasdeeq_call_id")
    private TasdeeqCallsHistory tasdeeqCall;

}
