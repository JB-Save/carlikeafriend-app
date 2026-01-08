package com.carlikeafriend_backend.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AppConfigValidator {

    private static final Logger logger = LoggerFactory.getLogger(AppConfigValidator.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${app.frontend.login-url}")
    private String loginUrl;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;


    @PostConstruct
    public void validateConfig() {
        logger.info("Verificando integridad de la configuración...");

        validate(uploadDir, "UPLOAD_DIR", true);
        validate(loginUrl, "FRONTEND_URL", true);
        validate(dbUrl, "DB_URL", true);
        validate(dbUsername, "DB_USER", true);
        validate(dbPassword, "DB_PASSWORD", false); // Sí creaste contraseña en la DB cambia a 'true'
        validate(jwtSecret, "JWT_SECRET", true);
        validate(mailUsername, "MAIL_USER", true);
        validate(mailPassword, "MAIL_PASSWORD", true);

        logger.info("Configuración cargada correctamente.");
    }

    private void validate(String value, String name, boolean isRequired) {
        // Si no es obligatorio y está vacío, permitimos que pase
        if (!isRequired && (value == null || value.isEmpty())) {
            return;
        }

        if (value == null || value.isBlank() || value.contains("${" + name + "}")) {
            String msg = "ERROR: La variable de entorno " + name + " no está definida.";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
    }
}