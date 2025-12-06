package com.episen.ms_product.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.episen.ms_product.application.dto.ProductRequestDTO;
import com.episen.ms_product.application.dto.ProductResponseDTO;
import com.episen.ms_product.application.mapper.ProductMapper;
import com.episen.ms_product.domain.entity.Product;
import com.episen.ms_product.domain.repository.ProductRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.episen.ms_product.infrastructure.exception.ResourceAlreadyExistsException;
import com.episen.ms_product.infrastructure.exception.ResourceNotFoundException;

/**
 * Service pour la gestion des produits.
 * Best practices :
 * - @Transactional pour la gestion des transactions
 * - Logging avec SLF4J
 * - Métriques personnalisées avec Micrometer
 * - Gestion d'erreurs explicite avec exceptions métier
 * - Séparation de la logique métier du contrôleur
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MeterRegistry meterRegistry;

    /**
     * Récupère tous les produits
     */
    public List<ProductResponseDTO> getAllProducts() {
        log.debug("Récupération de tous les produits");
        
        List<Product> products = productRepository.findAll();
        
        log.info("Nombre de produits récupérés: {}", products.size());
        
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un produit par son ID
     */
    public ProductResponseDTO getProductById(Long id) {
        log.debug("Récupération du produit avec l'ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        log.info("Produit trouvé: {}", product.getName());

        return productMapper.toDto(product);
    }

    /**
     * Crée un nouveau produit
     * (Pas besoin de faire +1 au stock car on créé un modle de produit : stock est à entrer par l'user)
     */
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        log.debug("Création d'un nouveau produit : {}", productRequestDTO.getName());
        
        // Vérifier si le produit existe déjà via son nom
        if (productRepository.existsByName(productRequestDTO.getName())) {
            log.warn("Tentative de création d'un produit avec un nom existant : {}",
                    productRequestDTO.getName());
            throw new ResourceAlreadyExistsException("Product", "name", productRequestDTO.getName());
        }

        Product product = productMapper.toEntity(productRequestDTO);
        Product savedProduct = productRepository.save(product);
        
        // Métrique personnalisée : nombre de produits créés
        Counter.builder("products.created")
                .description("Nombre de produits créés")
                .tag("type", "Product")
                .register(meterRegistry)
                .increment();
        
        log.info("Produit créé avec succès: ID={}, Nom={}", savedProduct.getId(), savedProduct.getName());
        
        return productMapper.toDto(savedProduct);
    }
    
}
