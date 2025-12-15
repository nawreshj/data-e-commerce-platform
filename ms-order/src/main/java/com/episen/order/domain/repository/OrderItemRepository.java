package com.episen.order.domain.repository;

import com.episen.order.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Récupère tous les items d'une commande --> detail d'une commande .
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * (Optionnel) Récupère les items correspondant à un produit.
     * Utile si un jour tu veux vérifier si un produit est encore "utilisé".
     */
    List<OrderItem> findByProductId(Long productId);
}