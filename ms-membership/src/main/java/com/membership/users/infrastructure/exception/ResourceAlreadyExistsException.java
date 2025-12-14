package com.membership.users.infrastructure.exception;

/**
 * Exception levée lorsqu'on tente de créer une ressource qui existe déjà.
 * Best practice : Exceptions métier spécifiques
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s existe déjà avec %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
