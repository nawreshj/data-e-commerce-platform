package com.episen.order.application.dto;

import java.math.BigDecimal;
import lombok.*;



/**
 * DTO représentant un utilisateur tel que renvoyé par ms-user.
 *
 * Utilisé exclusivement pour :
 *  - vérifier l’existence d’un utilisateur lors de la création d’une commande,
 *  - récupérer des informations minimales d’identification.
 *
 * Il s’agit d’un objet d’échange (projection externe), distinct de toute entité locale.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}