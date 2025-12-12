package com.episen.order.domain.enums;

/**
 * Statuts possibles d'une commande.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}