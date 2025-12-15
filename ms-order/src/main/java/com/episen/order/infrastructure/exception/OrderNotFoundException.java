package com.episen.order.infrastructure.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("Commande introuvable pour id=" + orderId);
    }
}