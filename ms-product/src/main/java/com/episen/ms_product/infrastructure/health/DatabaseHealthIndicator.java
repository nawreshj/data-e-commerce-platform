package com.episen.ms_product.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import com.episen.ms_product.domain.repository.ProductRepository;

/**
 * Health Indicator personnalisé pour vérifier l'état de la base de données.
 * Best practices :
 * - Implémente HealthIndicator pour les checks personnalisés
 * - Fournit des détails utiles pour le debugging
 * - Gère les exceptions proprement
 * - Utilisé par /actuator/health
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final ProductRepository productRepository;

    @Override
    public Health health() {
        try {
            // Vérifie la connexion à la base de données
            long productCount = productRepository.count();
            
            log.debug("Health check database - Total products: {}", 
                    productCount);
            
            return Health.up()
                    .withDetail("database", "H2")
                    .withDetail("status", "Connection OK")
                    .withDetail("totalUsers", 
                            productCount)
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
