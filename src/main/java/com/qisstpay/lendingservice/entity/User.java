package com.qisstpay.lendingservice.entity;

import com.qisstpay.lendingservice.enums.StatusType;
import com.qisstpay.lendingservice.enums.UserType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "credential_file_url")
    private String credentialFileUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<LenderCallLog> lenderCallLogs;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<MFBCallLog> mfbCallLogs;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "user_name", unique = true)
    private String userName;

    @Column(name = "ucid", unique = true)
    private String ucid;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;
}
