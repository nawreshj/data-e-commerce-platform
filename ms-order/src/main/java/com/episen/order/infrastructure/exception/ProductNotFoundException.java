package com.episen.order.infrastructure.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Produit introuvable pour id=" + productId);
    }
}