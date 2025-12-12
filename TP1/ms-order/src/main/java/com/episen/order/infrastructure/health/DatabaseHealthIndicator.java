package com.episen.order.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.episen.order.domain.repository.OrderRepository;

/**
 * Health Indicator personnalisé pour vérifier l'état de la base de données Orders.
 *
 * - Vérifie l'accès à la base H2
 * - Vérifie le bon fonctionnement de JPA
 * - Exposé via /actuator/health
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final OrderRepository orderRepository;

    @Override
    public Health health() {
        try {
            long orderCount = orderRepository.count();

            log.debug("Health check database - Total orders: {}", orderCount);

            return Health.up()
                    .withDetail("database", "H2")
                    .withDetail("status", "Connection OK")
                    .withDetail("totalOrders", orderCount)
                    .build();

        } catch (Exception e) {
            log.error("Health check database failed", e);

            return Health.down()
                    .withDetail("database", "H2")
                    .withDetail("status", "Connection Failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}