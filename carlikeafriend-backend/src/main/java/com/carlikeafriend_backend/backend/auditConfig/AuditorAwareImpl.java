package com.carlikeafriend_backend.backend.auditConfig;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 1. Obtenemos la autenticación del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Validamos si hay alguien logueado y no es un usuario anónimo
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            // Retornamos un valor por defecto para acciones de sistema (ej: registro inicial)
            return Optional.of("SYSTEM_USER");
        }

        // 3. Retornamos el email (o username) del usuario autenticado
        // Spring JPA usará este String para llenar los campos 'createdBy' y 'modifiedBy'
        return Optional.of(authentication.getName());
    }
}
