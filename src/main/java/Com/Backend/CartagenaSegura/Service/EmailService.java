package Com.Backend.CartagenaSegura.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private final Resend resend;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String mailFrom;

    public EmailService(@Value("${resend.api-key}") String apiKey,
                        TemplateEngine templateEngine) {
        this.resend = new Resend(apiKey);
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

            // Se genera el HTML desde la plantilla
            String html = templateEngine.process("EmailBienvenida", context);

            // Construir el paquete a través del Builder de Resend
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Cartagena Segura <" + mailFrom + ">")
                    .to(to)
                    .subject("¡Bienvenido a Cartagena Segura! 🛡️")
                    .html(html)
                    .build();

            // Ejecutar el envío
            CreateEmailResponse data = resend.emails().send(params);
            
            System.out.println("Email enviado existosamente por Resend. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error enviando email vía Resend a " + to);
            System.err.println("Detalle del Error Resend: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error procesando plantilla de correo a " + to + ": " + e.getMessage());
        }
    }
}
