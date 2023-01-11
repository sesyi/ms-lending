package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.ServiceType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "configurations")
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="lender_user_id")
    private User lenderUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @Column(name = "charge")
    private Double charge;

    @Column(name = "description")
    private String description;

    @Column(name = "credentials")
    private String credentials;

    @Column(name = "active_status")
    private Boolean activeStatus;

    @Column(name = "is_default")
    private Boolean defaultValue;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
