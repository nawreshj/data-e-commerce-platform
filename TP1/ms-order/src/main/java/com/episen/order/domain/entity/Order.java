package com.episen.order.domain.entity;


import com.episen.order.domain.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 - id: Long
- userId: Long (référence vers User)
- orderDate: LocalDateTime
- status: OrderStatus (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- totalAmount: BigDecimal
- shippingAddress: String (obligatoire)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
 */


@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UserID --> Référence vers l'utilisateur (ms-Membership) */
    @NotNull(message = "L'utilisateur est obligatoire")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    /** Montant total de la commande */
    @NotNull(message = "Le totalAmount est obligatoire")
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    @Column(name = "shipping_address", nullable = false, length = 300)
    private String shippingAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relation : Une commande contient plusieurs OrderItems.
     * Cascade = ALL → persiste les items quand on sauvegarde une commande
     * orphanRemoval = true → supprime automatiquement les items orphelins
     */
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items;
}