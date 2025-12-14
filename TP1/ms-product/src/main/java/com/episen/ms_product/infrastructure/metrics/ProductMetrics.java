package com.episen.ms_product.infrastructure.metrics;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.episen.ms_product.domain.repository.ProductRepository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralise les métriques Micrometer liées aux produits.
 * Objectif :
 * - Counters business (création, changement de statut)
 * - Gauge : montant total des commandes du jour
 */
@Slf4j
@Component
public class ProductMetrics {

    private final MeterRegistry meterRegistry;
    private final ProductRepository productRepository;

        private final ZoneId zoneId = ZoneId.of("Europe/Paris");

    // valeur exposée par la Gauge
    private volatile double amountToday = 0.0;

    public ProductMetrics(MeterRegistry meterRegistry,
                        ProductRepository productRepository) {

        this.meterRegistry = meterRegistry;
        this.productRepository = productRepository;
    }
    
}
