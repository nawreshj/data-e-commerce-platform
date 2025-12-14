package com.episen.order.infrastructure.metrics;

import com.episen.order.domain.enums.OrderStatus;
import com.episen.order.domain.repository.OrderRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Centralise les métriques Micrometer liées aux commandes.
 * Objectif :
 * - Counters business (création, changement de statut)
 * - Gauge : montant total des commandes du jour
 */
@Slf4j
@Component
public class OrderMetrics {

    private final MeterRegistry meterRegistry;
    private final OrderRepository orderRepository;

    private final ZoneId zoneId = ZoneId.of("Europe/Paris");

    // valeur exposée par la Gauge
    private volatile double amountToday = 0.0;

    public OrderMetrics(MeterRegistry meterRegistry,
                        OrderRepository orderRepository) {

        this.meterRegistry = meterRegistry;
        this.orderRepository = orderRepository;

        // Gauge : montant total des commandes du jour
        Gauge.builder("orders_amount_today", this, OrderMetrics::getAmountToday)
                .description("Montant total des commandes du jour")
                .baseUnit("currency")
                .tag("currency", "EUR")
                .register(meterRegistry);
    }

    /* =========================
       COUNTERS
       ========================= */

    /**
     * Compteur de commandes créées par statut
     * Exemple :
     * order_created_total{status="PENDING"} 5
     */
    public void incrementOrdersCreated(OrderStatus status) {
        Counter.builder("order_created_total")
                .description("Nombre total de commandes créées, groupées par statut")
                .tag("status", status.name())
                .register(meterRegistry)
                .increment();
    }

    /**
     * Compteur de changements de statut
     * Exemple :
     * order_status_changed_total{from="PENDING",to="DELIVERED"} 1
     */
    public void incrementOrderStatusChanged(OrderStatus from, OrderStatus to) {
        Counter.builder("order_status_changed_total")
                .description("Nombre total de changements de statut de commandes")
                .tag("from", from.name())
                .tag("to", to.name())
                .register(meterRegistry)
                .increment();
    }

    /* =========================
       GAUGE
       ========================= */

    public double getAmountToday() {
        return amountToday;
    }

    /**
     * Recalcule le montant total des commandes du jour
     * (toutes les 10 secondes — OK pour une démo)
     */
    @Scheduled(fixedDelay = 10_000)
    public void refreshAmountToday() {

        LocalDate today = LocalDate.now(zoneId);
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Double sum = orderRepository.sumTotalAmountBetween(start, end);
        amountToday = (sum == null) ? 0.0 : sum;

        log.debug("orders_amount_today updated to {}", amountToday);
    }
}