package com.tessitura.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tessitura.auth.enums.FailureReason;
import com.tessitura.auth.model.LoginAudit;
import com.tessitura.auth.model.User;

import java.util.List;

@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {

    List<LoginAudit> findByCity(String city);
    List<LoginAudit> findByCountry(String country);
    List<LoginAudit> findBySuccess(boolean success);
    List<LoginAudit> findByReason(FailureReason reason);
    List<LoginAudit> findTop10ByUserOrderByLoginTimeDesc(User user);
}