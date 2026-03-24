package com.ProAula.Cartagena_Segura.Service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class EmailService {

    private final SendGrid sendGrid;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String mailFrom;

    public EmailService(@Value("${sendgrid.api-key}") String apiKey,
                        TemplateEngine templateEngine) {
        this.sendGrid = new SendGrid(apiKey);
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

            Email from    = new Email(mailFrom, "Cartagena Segura");
            Email toEmail = new Email(to);
            Content content = new Content("text/html", html);
            Mail mail = new Mail(from, "¡Bienvenido a Cartagena Segura! 🛡️", toEmail, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            if (response.getStatusCode() >= 400) {
                System.err.println("Error SendGrid: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (IOException e) {
            System.err.println("Error enviando email de bienvenida a " + to + ": " + e.getMessage());
        }
    }
}