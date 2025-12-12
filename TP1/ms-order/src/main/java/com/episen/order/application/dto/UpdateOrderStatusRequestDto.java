package com.episen.order.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de changement de statut d'une commande.
 * Utilisé par l'endpoint: PUT /api/v1/orders/{id}/status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequestDto {

    @NotNull(message = "Le nouveau statut est obligatoire")
    private String status; // ex: "PENDING", "CONFIRMED", "SHIPPED", etc.
}