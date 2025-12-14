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
 */
@Slf4j
@Component
public class ProductMetrics {

    private final ProductRepository productRepository;

    private static final int LOW_STOCK_THRESHOLD = 5;

    public ProductMetrics(MeterRegistry meterRegistry, ProductRepository productRepository) {
        this.productRepository = productRepository;

        /**
         * Compteur du nombre total de produits
         */
        Gauge.builder("products.count", productRepository, ProductRepository::count)
                .description("Nombre total de produits")
                .register(meterRegistry);

        /**
         * Compteur nombre de produits actifs
         */
        Gauge.builder("products.active.count", productRepository, ProductRepository::countByActiveTrue)
                .description("Nombre de produits actifs")
                .register(meterRegistry);

        /**
         * Nombre de produits en rupture de stock
         */
        Gauge.builder("products.out_of_stock.count", productRepository, repo -> repo.countByStock(0))
                .description("Nombre de produits en rupture de stock (stock=0)")
                .register(meterRegistry);

        /**
         * Nombre de produits bientôt épuisés
         */
        Gauge.builder("products.low_stock.count", productRepository,
                repo -> repo.countByStockLessThan(LOW_STOCK_THRESHOLD))
                .description("Nombre de produits en stock faible (stock < seuil)")
                .register(meterRegistry);

        log.info("ProductMetrics gauges registered (low stock threshold={})", LOW_STOCK_THRESHOLD);
    }
}
