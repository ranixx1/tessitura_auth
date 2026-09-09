package com.tessitura.auth.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tessitura.auth.dto.ResetRequest;
import com.tessitura.auth.enums.PasswordChangeReason;
import com.tessitura.auth.exception.InvalidResetRequestException;
import com.tessitura.auth.model.PasswordHistory;
import com.tessitura.auth.model.PasswordReset;
import com.tessitura.auth.model.User;
import com.tessitura.auth.repository.PasswordHistoryRepository;
import com.tessitura.auth.repository.PasswordResetRepository;
import com.tessitura.auth.repository.UserRepository;

@Service
@Transactional
public class PasswordResetService {

    private static final int PASSWORD_HISTORY_LIMIT = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository passwordHistoryRepository;

    public PasswordResetService(
            PasswordResetRepository passwordResetRepository,
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            PasswordHistoryRepository passwordHistoryRepository) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordHistoryRepository = passwordHistoryRepository;
    }

    public String passwordReset(ResetRequest request) {
        User user = userRepository
                .findByEmailOrUsername(request.getReset(), request.getReset())
                .orElseThrow(() -> new InvalidResetRequestException("Invalid request"));

        if (!user.getBirthDate().equals(request.getBirthDate())) {
            throw new InvalidResetRequestException("Invalid request");
        }

        passwordResetRepository.findActiveTokenByUser(user, LocalDateTime.now())
                .ifPresent(old -> {
                    old.setUsed(true);
                    passwordResetRepository.save(old);
                });

        PasswordReset token = new PasswordReset();
        token.setUser(user);
        token.setToken(generateSecureToken());
        token.setExpireAt(LocalDateTime.now().plusMinutes(30));
        token.setUsed(false);

        passwordResetRepository.save(token);
        emailService.sendResetLink(user.getEmail(), token.getToken());

        return "Recovery email sent";
    }

    public void completePasswordReset(String tokenValue, String newPassword) {
        PasswordReset resetToken = passwordResetRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidResetRequestException("Invalid or expired token"));

        if (resetToken.isUsed() || resetToken.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new InvalidResetRequestException("Invalid or expired token");
        }

        User user = resetToken.getUser();

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidResetRequestException("New password cannot be the same as the current password");
        }

        List<PasswordHistory> histories = passwordHistoryRepository
                .findTop5ByUserOrderByChangedAtDesc(user);

        for (PasswordHistory history : histories) {
            if (history.getOldPassword() != null
                    && passwordEncoder.matches(newPassword, history.getOldPassword())) {
                throw new InvalidResetRequestException("This password has been used recently");
            }
        }

        String oldPasswordEncrypted = user.getPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetRepository.save(resetToken);

        prunePasswordHistory(user);

        PasswordHistory history = new PasswordHistory();
        history.setUser(user);
        history.setOldPassword(oldPasswordEncrypted);
        history.setReason(PasswordChangeReason.RESET_PASSWORD);
        passwordHistoryRepository.save(history);
    }

    private void prunePasswordHistory(User user) {
        List<PasswordHistory> all = passwordHistoryRepository
                .findByUserOrderByChangedAtDesc(user);
        if (all.size() >= PASSWORD_HISTORY_LIMIT) {
            passwordHistoryRepository.deleteAll(all.subList(PASSWORD_HISTORY_LIMIT - 1, all.size()));
        }
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}