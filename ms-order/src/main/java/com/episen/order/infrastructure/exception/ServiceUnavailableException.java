package com.episen.order.infrastructure.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String serviceName) {
        super(serviceName + " indisponible");
    }
}