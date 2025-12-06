package com.episen.ms_product.application.dto;

import java.math.BigDecimal;

import com.episen.ms_product.domain.entity.ProductCategory;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'un produit.
 * Best practices :
 * - Séparation des DTOs Request/Response
 * - Validation au niveau DTO
 * - Utilisation de Builder pattern
 * - Pas d'exposition de l'entité directement dans l'API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
    
    @NotBlank(message = "Le nom du produit ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le nom du produit doit contenir entre 3 et 100 caractères")
    private String name;

    @NotBlank(message = "La description du produit ne peut pas être vide")
    @Size(min = 2, max = 50, message = "La description du produit doit contenir entre 10 et 500 caractères")
    private String description;

    @NotNull(message = "Le prix du produit est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être strictement supérieur à 0")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock doit être supérieur ou égal à 0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull(message = "La catégorie est obligatoire (ELECTRONICS, BOOKS, FOOD, OTHER)")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ProductCategory category;
    
}
