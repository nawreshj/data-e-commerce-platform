package com.episen.ms_product.infrastructure.metrics;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.episen.ms_product.domain.repository.ProductRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductMetrics {

        private final ProductRepository productRepository;
        private static final int LOW_STOCK_THRESHOLD = 5;

        private final AtomicLong totalCount = new AtomicLong(0);
        private final AtomicLong activeCount = new AtomicLong(0);
        private final AtomicLong outOfStockCount = new AtomicLong(0);
        private final AtomicLong lowStockCount = new AtomicLong(0);

        public ProductMetrics(MeterRegistry meterRegistry, ProductRepository productRepository) {
                this.productRepository = productRepository;

                Gauge.builder("products.count", totalCount, AtomicLong::get)
                                .description("Nombre total de produits")
                                .register(meterRegistry);

                Gauge.builder("products.active.count", activeCount, AtomicLong::get)
                                .description("Nombre de produits actifs")
                                .register(meterRegistry);

                Gauge.builder("products.out_of_stock.count", outOfStockCount, AtomicLong::get)
                                .description("Nombre de produits en rupture de stock")
                                .register(meterRegistry);

                Gauge.builder("products.low_stock.count", lowStockCount, AtomicLong::get)
                                .description("Nombre de produits en stock faible")
                                .register(meterRegistry);
                refreshMetrics();
        }

  
        @Scheduled(fixedRate = 60000)
        public void refreshMetrics() {
                log.debug("Mise à jour des métriques produits...");

                try {
                        totalCount.set(productRepository.count());
                        activeCount.set(productRepository.countByActiveTrue());
                        outOfStockCount.set(productRepository.countByStock(0));
                        lowStockCount.set(productRepository.countByStockLessThan(LOW_STOCK_THRESHOLD));
                } catch (Exception e) {
                        log.error("Erreur lors du rafraîchissement des métriques", e);
                }
        }
}