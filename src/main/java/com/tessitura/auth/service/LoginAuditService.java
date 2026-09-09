package com.tessitura.auth.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tessitura.auth.model.LoginAudit;
import com.tessitura.auth.repository.LoginAuditRepository;

@Service
public class LoginAuditService {

    private final LoginAuditRepository repository;

    public LoginAuditService(LoginAuditRepository repository) {
        this.repository = repository;
    }

    @Async
    public void save(LoginAudit audit) {
        repository.save(audit);
    }
}