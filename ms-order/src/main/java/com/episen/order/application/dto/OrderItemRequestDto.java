package com.episen.order.application.dto;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO représentant une ligne de commande pour la création d'une commande.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDto {

    @NotNull(message = "Le productId est obligatoire")
    private Long productId;

    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;
}