package com.episen.order.application.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO représentant une ligne de commande dans les réponses de l’API.
 *
 * Ce DTO est utilisé pour exposer les données d’un OrderItem au client
 * (ex : après la création d’une commande ou lors d'une consultation).
 *
 *Ce DTO NE représente pas l'entité JPA OrderItem directement :
 *     - il ne contient que les informations nécessaires à l’extérieur du service
 *     - il ne transporte aucune logique métier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {

    /** Identifiant technique de la ligne de commande */
    private Long id;

    /** Identifiant du produit associé à cette ligne */
    private Long productId;

    /** Nom du produit (copié depuis ms-product pour éviter un appel externe supplémentaire) */
    private String productName;

    /** Quantité commandée pour ce produit */
    private Integer quantity;

    /** Prix unitaire au moment de la commande (en BigDecimal pour éviter les erreurs de calcul monétaire) */
    private BigDecimal unitPrice;

    /** Sous-total : unitPrice * quantity */
    private BigDecimal subtotal;
}