package com.tessitura.auth.model;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Getter;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.ManyToOne;

import lombok.Setter;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter

public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    private LocalDateTime expireAt;
    private LocalDateTime createdAt;

    private boolean isUsed = false;
    

    @PrePersist
    public void PrePersist(){
        this.createdAt = LocalDateTime.now();
    }
}

