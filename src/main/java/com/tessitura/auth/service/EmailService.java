package com.tessitura.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetLink(String email, String token) {

        String link = frontendUrl +
                "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Redefinição de senha");
        message.setText("""
                Olá,

                Recebemos uma solicitação para redefinir sua senha.

                Clique no link:

                %s

                Caso não tenha solicitado, ignore este email.

                """.formatted(link));

        try {
            mailSender.send(message);
            System.out.println("EMAIL ENVIADO");
        } catch (Exception e) {
            System.out.println("ERRO EMAIL:");
            e.printStackTrace();
            throw e;
        }
    }
}