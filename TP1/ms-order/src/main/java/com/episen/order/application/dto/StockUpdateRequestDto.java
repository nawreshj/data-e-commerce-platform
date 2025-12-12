package com.episen.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilisé par ms-order pour appeler l'endpoint PATCH /api/v1/products/{id}/stock
 * du microservice ms-product.
 *
 * JSON envoyé :
 * {
 *   "newStock": 42
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateRequestDto {

    /**
     * Nouveau stock à enregistrer pour le produit.
     */
    private Integer newStock;
}