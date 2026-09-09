package com.tessitura.auth.model;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tessitura.auth.enums.FailureReason;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "login_audit")
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class LoginAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime loginTime;

    private String ipAddress;
    private String country;
    private String city;

    private String userAgent; // navegador/dispositivo

    private boolean success; // true = login OK, false = erro

    @Enumerated(EnumType.STRING)
    private FailureReason reason; 
}