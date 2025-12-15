package com.episen.order.infrastructure.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("Utilisateur introuvable pour id=" + userId);
    }
}