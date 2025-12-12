package com.episen.ms_product.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité Product représentant un produit dans le système.
 * Best practices :
 * - Utilisation de Lombok pour réduire le boilerplate
 * - Validation avec Bean Validation
 * - Audit automatique avec @CreationTimestamp et @UpdateTimestamp
 * - Builder pattern pour une construction flexible
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit ne peut pas être vide")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "La description du produit ne peut pas être vide")
    @Size(min = 10, max = 500, message = "La description doit contenir entre 10 et 500 caractères")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @NotNull(message = "Le prix du produit est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être strictement supérieur à 0")
    @Column(name = "price", nullable = false, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock doit être supérieur ou égal à 0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull(message = "La catégorie est obligatoire (ELECTRONICS, BOOKS, FOOD, OTHER)")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ProductCategory category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "active")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}