package com.carlikeafriend_backend.backend.security;

import com.carlikeafriend_backend.backend.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 Maneja el error 401 cuando la excepción ocurre en la cadena de filtros de Spring Security
 (antes de llegar al Controller), por ejemplo: falta de token.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Para manejar LocalDateTime correctamente
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        // Establecer el estado HTTP a 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Establecer el tipo de contenido a JSON
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        // Escribir el cuerpo de la respuesta JSON
        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                "Acceso denegado. Debe iniciar sesión para acceder a este recurso.",
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
