package com.tessitura.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tessitura.auth.enums.PasswordChangeReason;
import com.tessitura.auth.model.PasswordHistory;
import com.tessitura.auth.model.User;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> findByUser(User user);

    List<PasswordHistory> findByReason(PasswordChangeReason reason);

    List<PasswordHistory> findByUserOrderByChangedAtDesc(User user);

    List<PasswordHistory> findTop5ByUserOrderByChangedAtDesc(User user);
}
