package com.crmtech360.crmtech360_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Estructura estándar para devolver información de errores en la API.
 * Incluye detalles útiles para el cliente sobre el error ocurrido.
 */
public class ApiErrorResponseDTO {

    @Schema(description = "Fecha y hora en que ocurrió el error.", example = "2024-05-15T10:30:45.123Z")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP.", example = "404")
    private int status;

    @Schema(description = "Descripción corta del error HTTP.", example = "Not Found")
    private String error;

    @Schema(description = "Mensaje explicativo del error.", example = "El recurso solicitado no fue encontrado.")
    private String message;

    @Schema(description = "Ruta de la API donde ocurrió el error.", example = "/api/v1/clientes/999")
    private String path;

    @Schema(description = "Errores de validación por campo, si aplica.")
    private Map<String, String> validationErrors;

    @Schema(description = "Lista de detalles adicionales del error, si existen.")
    private List<String> details;

    // Constructores
    public ApiErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponseDTO(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ApiErrorResponseDTO(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this(status, error, message, path);
        this.validationErrors = validationErrors;
    }

    public ApiErrorResponseDTO(int status, String error, String message, String path, List<String> details) {
        this(status, error, message, path);
        this.details = details;
    }

    // Getters y setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Map<String, String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}