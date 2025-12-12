package com.episen.ms_product.application.mapper;

import org.springframework.stereotype.Component;
import com.episen.ms_product.domain.entity.Product;
import com.episen.ms_product.application.dto.ProductRequestDTO;
import com.episen.ms_product.application.dto.ProductResponseDTO;

/**
 * Mapper pour convertir entre Product et ses DTOs.
 * Best practices :
 * - Séparation de la logique de mapping
 * - Conversion centralisée
 * - Facilite les tests unitaires
 */
@Component
public class ProductMapper {

    /**
     * Convertit un ProductRequestDTO en entité Product
     */
    public Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(dto.getCategory())
                .build();
    }

    /**
     * Convertit une entité Product en ProductResponseDTO
     */
    public ProductResponseDTO toDto(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Met à jour une entité Product existante avec les données du DTO
     */
    public void updateEntityFromDto(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
    }
}
