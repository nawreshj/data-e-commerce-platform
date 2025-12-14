package com.episen.order.domain.entity;




import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
- id: Long
- orderId: Long
- productId: Long (référence vers Product)
- productName: String (copie du nom au moment de la commande)
- quantity: Integer (> 0)
- unitPrice: BigDecimal
- subtotal: BigDecimal (quantity * unitPrice)
 */

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Référence vers la commande  */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Id du produit au moment de la commande (référence ms-Product) */
    @NotNull(message = "Le productId est obligatoire")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** Copie du nom du produit au moment de la commande */
    @NotNull(message = "Le productName est obligatoire")
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** Prix unitaire au moment de l'achat */
    @NotNull(message = "Le prix unitaire est obligatoire")
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    /** quantity * unitPrice */
    @NotNull(message = "Le subtotal est obligatoire")
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    
}