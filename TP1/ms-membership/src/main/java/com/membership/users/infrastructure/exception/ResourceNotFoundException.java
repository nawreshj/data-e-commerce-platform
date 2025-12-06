package com.membership.users.infrastructure.exception;

/**
 * Exception levée lorsqu'une ressource n'est pas trouvée.
 * Best practice : Exceptions métier spécifiques pour un meilleur traitement
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s non trouvé avec %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
