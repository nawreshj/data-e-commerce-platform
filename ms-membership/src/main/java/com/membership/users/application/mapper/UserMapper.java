package com.membership.users.application.mapper;

import org.springframework.stereotype.Component;

import com.membership.users.application.dto.UserRequestDTO;
import com.membership.users.application.dto.UserResponseDTO;
import com.membership.users.domain.entity.User;

/**
 * Mapper pour convertir entre User et ses DTOs.
 * Best practices :
 * - Séparation de la logique de mapping
 * - Conversion centralisée
 * - Facilite les tests unitaires
 */
@Component
public class UserMapper {

    /**
     * Convertit un UserRequestDTO en entité User
     */
    public User toEntity(UserRequestDTO dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .active(true)
                .build();
    }

    /**
     * Convertit une entité User en UserResponseDTO
     */
    public UserResponseDTO toDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Met à jour une entité User existante avec les données du DTO
     */
    public void updateEntityFromDto(UserRequestDTO dto, User user) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
    }
}
