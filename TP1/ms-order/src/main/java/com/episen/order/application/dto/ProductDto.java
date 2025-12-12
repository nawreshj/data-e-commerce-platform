package com.episen.order.application.dto;

import java.math.BigDecimal;
import lombok.*;



/**
 * DTO utilisé pour représenter un produit tel que renvoyé par ms-product.
 *
 * Il sert uniquement pour :
 *  - vérifier l’existence d’un produit,
 *  - récupérer prix, nom et stock,
 *  - appliquer les règles métier lors de la création d’une commande.
 *
 * Ce n’est pas une entité : c’est une projection externe utilisée
 * pour les appels REST sortants vers ms-product.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    // si besoin plus tard : category, active, etc.
}