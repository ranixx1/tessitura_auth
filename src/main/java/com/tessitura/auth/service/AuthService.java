package com.tessitura.auth.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tessitura.auth.dto.LoginRequest;
import com.tessitura.auth.dto.RegisterRequest;
import com.tessitura.auth.enums.FailureReason;
import com.tessitura.auth.enums.Type;
import com.tessitura.auth.exception.UnauthorizedException;
import com.tessitura.auth.exception.UserAlreadyExistsException;
import com.tessitura.auth.model.LoginAudit;
import com.tessitura.auth.model.User;
import com.tessitura.auth.repository.LoginAuditRepository;
import com.tessitura.auth.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Transactional
public class AuthService {

    private static final String DUMMY_HASH = "$2a$10$W9XmPlWyLJy9KQTT/ml03eAzi3Z8Q2Hp0Ikgw/3ASzmE9It0xPZvy";

    private final UserRepository userRepository;
    private final LoginAuditRepository loginAuditRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, LoginAuditRepository loginAuditRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.loginAuditRepository = loginAuditRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()
                || userRepository.findByEmail(request.getEmail()).isPresent())
            throw new UserAlreadyExistsException("Username or e-mail already in use");

        User user = new User();
        user.setName(request.getName());
        user.setBirthDate(request.getBirthDate());
        user.setUsername(request.getUsername());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setType(Type.ROLE_NORMAL);
        user.setActive(true);
        user.setFailedAttempts(0);

        userRepository.save(user);
    }

    public String login(LoginRequest request, String ipAddress, String userAgent) {

        User user = userRepository
                .findByEmailOrUsername(request.getLogin(), request.getLogin())
                .orElse(null);

        // elimina timing attack
        String hashToCheck = (user != null) ? user.getPassword() : DUMMY_HASH;
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), hashToCheck);

        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!user.isActive()) {
            saveAudit(user, false, ipAddress, FailureReason.ACCOUNT_DISABLED, userAgent);
            throw new UnauthorizedException("Invalid credentials");
        }

        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            saveAudit(user, false, ipAddress, FailureReason.ACCOUNT_LOCKED, userAgent);
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!passwordMatches) {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);
            if (attempts >= 5) {
                user.setLockUntil(LocalDateTime.now().plusMinutes(5));
            }
            userRepository.save(user);
            saveAudit(user, false, ipAddress, FailureReason.BAD_CREDENTIALS, userAgent);
            throw new UnauthorizedException("Invalid credentials");
        }

        user.setFailedAttempts(0);
        user.setLockUntil(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        saveAudit(user, true, ipAddress, null, userAgent);
        return jwtService.generateToken(user);
    }

    private void saveAudit(User user, boolean success, String ipAddress,
            FailureReason reason, String userAgent) {
        LoginAudit audit = new LoginAudit();
        audit.setUser(user);
        audit.setLoginTime(LocalDateTime.now());
        audit.setIpAddress(ipAddress);
        audit.setUserAgent(userAgent);
        audit.setSuccess(success);
        audit.setReason(reason);
        loginAuditRepository.save(audit);
    }
}