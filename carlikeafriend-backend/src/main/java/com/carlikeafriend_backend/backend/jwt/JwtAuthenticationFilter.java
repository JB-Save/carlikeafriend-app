package com.carlikeafriend_backend.backend.jwt;

import com.carlikeafriend_backend.backend.exception.JwtValidationException;
import com.carlikeafriend_backend.backend.service.IJwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final IJwtService jwtService;
    private final UserDetailsService userDetailsService;
    // Inyectar el HandlerExceptionResolver para delegar el manejo de la excepción
    private final HandlerExceptionResolver resolver;

    @Autowired
    public JwtAuthenticationFilter(IJwtService jwtService, UserDetailsService userDetailsService,
                                   // Usamos @Qualifier para especificar el nombre del bean del resolver
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (ExpiredJwtException e) {
            // El token ha expirado.
            logger.warn("JWT token ha expirado para la solicitud a {}: {}", request.getRequestURI(), e.getMessage());

            // Delegar el manejo de la excepción al HandlerExceptionResolver
            // Lanzamos JwtValidationException que envuelve la excepción original.
            resolver.resolveException(request, response, null, new JwtValidationException("Token JWT expirado: " + e.getMessage(), e));

            // No establecemos autenticación, la petición continuará sin estar autenticada.
            return; // Detenemos la cadena de filtros aquí

        } catch (SignatureException e) {
            // El token tiene una firma inválida.
            logger.error("Falló la validación de la firma JWT para la solicitud a {}: {}", request.getRequestURI(), e.getMessage());

            // Delegar el manejo de la excepción
            resolver.resolveException(request, response, null, new JwtValidationException("Firma del token JWT inválida: " + e.getMessage(), e));

            return; // Detenemos la cadena de filtros aquí

        } catch (Exception e) {
            // Otra excepción de JWT (malformado, etc.)
            logger.error("Error al procesar el token JWT para la solicitud a {}: {}", request.getRequestURI(), e.getMessage());

            // Delegar el manejo de otras excepciones de JWT
            resolver.resolveException(request, response, null, new JwtValidationException("Token JWT inválido o malformado: " + e.getMessage(), e));

            return; // Detenemos la cadena de filtros aquí
        }

        filterChain.doFilter(request, response);
    }
}
