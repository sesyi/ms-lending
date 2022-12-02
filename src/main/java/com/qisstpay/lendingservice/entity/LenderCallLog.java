package com.qisstpay.lendingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qisstpay.lendingservice.enums.CallStatusType;
import com.qisstpay.lendingservice.enums.CallType;
import com.qisstpay.lendingservice.enums.ServiceType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "lender_calls_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LenderCallLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "lender_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CallStatusType status;

    @Column(name = "call_type")
    @Enumerated(EnumType.STRING)
    private CallType callType;

    @Column(name = "request")
    private String request;

    @Column(name = "error")
    private String error;

    @CreationTimestamp
    @Column(name = "requested_at")
    private Timestamp requestedAt;

    @UpdateTimestamp
    @Column(name = "response_at")
    private Timestamp responseAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lenderCall")
    private List<TasdeeqCallLog> tasdeeqCallLogs;
}
