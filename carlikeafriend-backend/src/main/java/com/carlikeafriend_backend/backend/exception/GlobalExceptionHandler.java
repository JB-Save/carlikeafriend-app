package com.carlikeafriend_backend.backend.exception;

import com.carlikeafriend_backend.backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Método auxiliar para construir la respuesta estandarizada
    private ResponseEntity<ErrorResponse> buildResponseEntity(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    // 400 BAD_REQUEST: Validaciones @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.warn("Error de Validaciones @Valid: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        String errorMessageDetails = String.join("; ", errors.values());
        //String firstErrorMessage = errors.values().stream().findFirst().orElse("Error de validación desconocido.");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errorMessageDetails,
                request.getRequestURI(),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 400 BAD_REQUEST: Límites de imagen y extensiones
    @ExceptionHandler({ImageLimitExceededException.class, InvalidFileExtensionException.class})
    public ResponseEntity<ErrorResponse> handleFileExceptions(RuntimeException ex, HttpServletRequest request) {
        logger.warn("Error de validación de archivo: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // 401 UNAUTHORIZED: Credenciales
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        logger.warn("Fallo de autenticación: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas o sesión inválida.", request);
    }

    // 401 UNAUTHORIZED: JWT Específico
    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ErrorResponse> handleJwtValidationException(JwtValidationException ex, HttpServletRequest request) {
        logger.warn("Error de validación JWT: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    // 403 FORBIDDEN: Manejo de AccessDeniedException
    // Se incluye para los casos en que la excepción se lanza *dentro* de un @Service o @Controller,
    // pero para las comprobaciones de la cadena de filtros de seguridad, RestAccessDeniedHandler es la opción principal.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Acceso denegado a {}", request.getRequestURI());
        return buildResponseEntity(HttpStatus.FORBIDDEN, "Acceso denegado. No tiene los permisos requeridos.", request);
    }

    // 404 NOT_FOUND: Para recursos no encontrados (ResourceNotFoundException)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        logger.info("Recurso no encontrado: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // 404 NOT_FOUND o 500 INTERNAL_SERVER_ERROR: Para errores de archivo/IO
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, HttpServletRequest request) {
        logger.error("Error de IO: {}", ex.getMessage(), ex);
        if (ex.getMessage() != null && ex.getMessage().contains("El archivo no se pudo encontrar")) {
            return buildResponseEntity(HttpStatus.NOT_FOUND, "El archivo solicitado no existe.", request); // 404 si el archivo no existe
        }
        // Para otras IOException (permisos, escritura), devolvemos 500
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar el archivo.", request);
    }

    // 409 CONFLICT: Violación de integridad (FKs)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        logger.error("Error de integridad de datos: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, "No se puede completar la operación porque el recurso está asociado a otros datos.", request);
    }

    // 409 CONFLICT: Nombres duplicados
    @ExceptionHandler(UniqueNameException.class)
    public ResponseEntity<ErrorResponse> handleUniqueNameException(UniqueNameException ex, HttpServletRequest request) {
        logger.warn("Conflicto de nombre único: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 413 PAYLOAD TOO LARGE: Manejo de excepción cuando el archivo supera el límite de application.properties
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(org.springframework.web.multipart.MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return buildResponseEntity(HttpStatus.PAYLOAD_TOO_LARGE, "El tamaño de los archivos enviados excede el límite permitido por el servidor (Máximo 30MB por solicitud).", request);
    }

    // 500 INTERNAL_SERVER_ERROR: Manejador genérico para cualquier otra Exception no mapeada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        logger.error("Error inesperado: ", ex);
        ex.printStackTrace(); // Imprimir el stack trace para el log del servidor
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor. Por favor contacte a soporte.", request);
    }
}
