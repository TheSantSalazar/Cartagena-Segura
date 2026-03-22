package com.ProAula.Cartagena_Segura.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendWelcomeEmail(String to, String username, String fullName) {
        try {
            Context context = new Context();
            context.setVariable("username",     username);
            context.setVariable("email",        to);
            context.setVariable("fullName",     fullName != null ? fullName : username);
            context.setVariable("registeredAt", LocalDateTime.now());
            context.setVariable("appUrl",       "https://cartagena-segura.vercel.app");

            String html = templateEngine.process("email-bienvenida", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@cartagenasegura.com");
            helper.setTo(to);
            helper.setSubject("¡Bienvenido a Cartagena Segura! 🛡️");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error sin interrumpir el flujo del registro
            System.err.println("Error enviando email de bienvenida a " + to + ": " + e.getMessage());
        }
    }
}
