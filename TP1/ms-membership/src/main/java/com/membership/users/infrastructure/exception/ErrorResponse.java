package com.membership.users.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Réponse d'erreur standardisée pour l'API.
 * Best practices :
 * - Structure cohérente pour toutes les erreurs
 * - Informations détaillées mais sans fuite de données sensibles
 * - Facilite le debugging côté client
 * - Conforme aux standards REST
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;
    
    private String error;
    
    private String message;
    
    private String path;
    
    private List<ValidationError> validationErrors;

    /**
     * Classe interne pour représenter les erreurs de validation
     */
    @Data
    @Builder
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
