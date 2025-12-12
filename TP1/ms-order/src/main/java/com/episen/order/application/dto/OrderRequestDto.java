package com.episen.order.application.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * DTO pour la création d'une commande.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    @NotNull(message = "L'utilisateur est obligatoire")
    private Long userId;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    private String shippingAddress;

    @NotEmpty(message = "Une commande doit contenir au moins un article")
    private List<OrderItemRequestDto> items;
}
