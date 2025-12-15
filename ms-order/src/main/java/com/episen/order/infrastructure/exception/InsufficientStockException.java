package com.episen.order.infrastructure.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId) {
        super("Stock insuffisant pour le produit id=" + productId);
    }
}