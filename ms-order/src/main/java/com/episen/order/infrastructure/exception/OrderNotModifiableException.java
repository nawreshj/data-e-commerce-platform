package com.episen.order.infrastructure.exception;

import com.episen.order.domain.enums.OrderStatus;

public class OrderNotModifiableException extends RuntimeException {

    public OrderNotModifiableException(OrderStatus status) {
        super("Impossible de modifier une commande " + status);
    }
}