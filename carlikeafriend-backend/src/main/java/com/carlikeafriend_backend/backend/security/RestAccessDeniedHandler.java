package com.carlikeafriend_backend.backend.security;

import com.carlikeafriend_backend.backend.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
  Maneja el error 403 cuando la excepción ocurre en la cadena de filtros de Spring Security
  (URL Security), por ejemplo: Rol insuficiente detectado por requestMatchers().
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        // Establecer el estado HTTP a 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // Establecer el tipo de contenido a JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Escribir el cuerpo de la respuesta JSON
        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "No tiene permisos suficientes para realizar esta acción.",
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
