package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.service.IEmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService implements IEmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // Lee el correo "desde" application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    //Envía el correo de confirmación de registro.
    //Es asíncrono para no bloquear el hilo de registro.
    @Override
    @Async
    public void sendRegistrationConfirmation(String userEmail, String username, String loginUrl) {
        try {
            logger.info("Iniciando preparación de correo para {}", userEmail);

            // Preparar el contexto de Thymeleaf (las variables para la plantilla)
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("email", userEmail);
            context.setVariable("loginUrl", loginUrl);

            // Procesar la plantilla HTML
            String htmlContent = templateEngine.process("email/registration-confirmation", context);

            // Crear el mensaje de correo
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(userEmail);
            helper.setFrom(fromEmail);
            helper.setSubject("¡Registro Exitoso! Bienvenido a Car Like A Friend");
            helper.setText(htmlContent, true); // true indica que es HTML

            // Enviar el correo
            mailSender.send(mimeMessage);
            logger.info("Correo de confirmación enviado exitosamente a {}", userEmail);

        } catch (MessagingException e) {
            logger.error("Error al enviar correo de confirmación a {}: {}", userEmail, e.getMessage());
        }
    }
}
