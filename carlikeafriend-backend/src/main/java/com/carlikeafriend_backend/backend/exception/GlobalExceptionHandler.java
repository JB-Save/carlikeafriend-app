package com.carlikeafriend_backend.backend.exception;

import com.carlikeafriend_backend.backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.time.LocalDateTime;
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

    // 400 BAD_REQUEST: Parámetro faltante (Error del cliente)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex, HttpServletRequest request) {
        logger.warn("Error de validación - Parámetro faltante: {}", ex.getParameterName());

        String name = ex.getParameterName();
        String message = String.format("El parámetro '%s' es obligatorio para procesar la solicitud.", name);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 400 BAD_REQUEST: Error de tipo incompatible (Error del cliente)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String name = ex.getName();
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        String valueSent = ex.getValue() != null ? ex.getValue().toString() : "nulo";

        String message = String.format("El parámetro '%s' recibió un valor inválido ('%s'). Se esperaba un tipo %s.",
                name, valueSent, expectedType);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Type Mismatch Error",
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 400 BAD_REQUEST: Límites de imagen y extensiones
    @ExceptionHandler({ImageLimitExceededException.class, InvalidFileExtensionException.class})
    public ResponseEntity<ErrorResponse> handleFileExceptions(RuntimeException ex, HttpServletRequest request) {
        logger.warn("Error de validación de archivo: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // 400 BAD_REQUEST: estado inválido
    @ExceptionHandler({InvalidResourceStateException.class})
    public ResponseEntity<ErrorResponse> handleStatusException(RuntimeException ex, HttpServletRequest request) {
        logger.warn("Error de validación: {}", ex.getMessage());
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
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request); //Ej: "No se puede completar la operación porque el recurso está asociado a otros datos."
    }

    // 409 CONFLICT: Recursos duplicados
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request) {
        logger.warn("Conflicto de recurso único: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 409 CONFLICT: Transición de estados inválida
    @ExceptionHandler(BookingStateConflictException.class)
    public ResponseEntity<ErrorResponse> handleTransitionStateException(BookingStateConflictException ex, HttpServletRequest request) {
        logger.warn("Transición de estados de la reserva inválido: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 409 CONFLICT: Transición de estados inválida
    @ExceptionHandler(InspectionStateConflictException.class)
    public ResponseEntity<ErrorResponse> handleTransitionStateException(InspectionStateConflictException ex, HttpServletRequest request) {
        logger.warn("Transición de estados de la inspección inválido: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 409 CONFLICT: Transición de estados inválida
    @ExceptionHandler(MaintenanceStateConflictException.class)
    public ResponseEntity<ErrorResponse> handleTransitionStateException(MaintenanceStateConflictException ex, HttpServletRequest request) {
        logger.warn("Transición de estados de Mantenimiento inválido: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 409 CONFLICT: Captura fallos de Bloqueo Pesimista (DB bloqueada o Timeout)
    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleLockingFailure(PessimisticLockingFailureException ex, HttpServletRequest request) {
        logger.warn("Conflicto de concurrencia - Bloqueo Pesimista: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 409 CONFLICT: Captura fallos de Bloqueo Optimista (@Version desactualizada)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        logger.warn("Conflicto de concurrencia - Bloqueo Optimista: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.CONFLICT, "Los datos fueron actualizados por otro usuario. Por favor, recarga la información.", request);
    }

    // 413 PAYLOAD TOO LARGE: Manejo de excepción cuando el archivo supera el límite de application.properties
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(org.springframework.web.multipart.MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return buildResponseEntity(HttpStatus.PAYLOAD_TOO_LARGE, "El tamaño de los archivos enviados excede el límite permitido por el servidor (Máximo 30MB por solicitud).", request);
    }

    // 422 UNPROCESSABLE_ENTITY: Rango de valores inválido (Error del cliente)
    @ExceptionHandler(InvalidRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDate(InvalidRangeException ex, HttpServletRequest request) {
        logger.warn("Rango de valores inválido: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    // 422 UNPROCESSABLE_ENTITY: Conflicto de disponibilidad (Estado del servidor)
    @ExceptionHandler(ResourceNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflict(ResourceNotAvailableException ex, HttpServletRequest request) {
        logger.warn("Conflicto de disponibilidad: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    // 500 INTERNAL_SERVER_ERROR: Manejador genérico para cualquier otra Exception no mapeada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        logger.error("Error inesperado: ", ex);
        ex.printStackTrace(); // Imprimir el stack trace para el log del servidor
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor. Por favor contacte a soporte.", request);
    }
}
