package com.episen.order.infrastructure.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Health Indicator personnalisé pour vérifier la disponibilité
 * des services externes dépendants :
 * - ms-product
 * - ms-user (membership)
 *
 * Best practices :
 * - Implémente HealthIndicator
 * - Vérifie des dépendances réelles via HTTP
 * - Fournit des détails exploitables dans /actuator/health
 * - Gère proprement les erreurs
 */
@Slf4j
@Component
public class ExternalServicesHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;

    @Value("${app.clients.product.actuator-url}")
    private String productHealthUrl;

    @Value("${app.clients.user.actuator-url}")
    private String userHealthUrl;

    public ExternalServicesHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {

        boolean productServiceUp = checkService(productHealthUrl);
        boolean userServiceUp = checkService(userHealthUrl);

        if (productServiceUp && userServiceUp) {
            return Health.up()
                    .withDetail("productService", "UP")
                    .withDetail("userService", "UP")
                    .build();
        }

        return Health.down()
                .withDetail("productService", productServiceUp ? "UP" : "DOWN")
                .withDetail("userService", userServiceUp ? "UP" : "DOWN")
                .build();
    }

    /**
     * Vérifie la disponibilité d’un service via son endpoint Actuator /health.
     */
    private boolean checkService(String healthUrl) {
        try {
            restTemplate.getForObject(healthUrl, String.class);
            log.debug("Health check OK for {}", healthUrl);
            return true;
        } catch (RestClientException ex) {
            log.error("Health check FAILED for {}", healthUrl, ex);
            return false;
        }
    }
}