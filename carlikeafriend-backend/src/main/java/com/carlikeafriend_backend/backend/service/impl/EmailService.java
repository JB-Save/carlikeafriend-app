package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.entity.Reservation;
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

import java.time.format.DateTimeFormatter;

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
    @Override
    @Async("emailExecutor") // Evita bloquear el hilo principal de la petición HTTP
    public void sendRegistrationConfirmation(String userEmail, String userName, String loginUrl) {
        try {
            logger.info("Iniciando preparación de correo para {}", userEmail);

            // Preparar el contexto de Thymeleaf (las variables para la plantilla)
            Context context = new Context();
            context.setVariable("userName", userName);
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

    //Envía el correo de confirmación de reserva.
    @Override
    @Async("emailExecutor")
    public void sendReservationConfirmation(Reservation reservation) {
        try {
            logger.info("Iniciando preparación de correo para confirmación de reserva {}", reservation.getId());

            // 1. Preparar las variables para la plantilla Thymeleaf
            Context context = new Context();
            context.setVariable("userName", reservation.getUser().getName() + " " + reservation.getUser().getLastName());
            context.setVariable("vehicleName", reservation.getVehicle().getProduct().getName());
            context.setVariable("reservationId", reservation.getId().toString());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            context.setVariable("pickupDate", reservation.getPickupDatetime().format(formatter));
            context.setVariable("returnDate", reservation.getReturnDatetime().format(formatter));
            context.setVariable("pickupBranch", reservation.getPickupBranch());

            // 2. Procesar la plantilla HTML
            String htmlContent = templateEngine.process("reservation-confirmation", context);

            // 3. Configurar y enviar el correo
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(reservation.getUser().getEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("¡Tu reserva en Car Like A Friend está confirmada!");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Correo de confirmación enviado exitosamente a {}", reservation.getUser().getEmail());

        } catch (MessagingException e) {
            logger.error("Error enviando correo de confirmación a {}: {}", reservation.getUser().getEmail(), e.getMessage());
        }
    }


}
