package com.episen.ms_product.application.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.episen.ms_product.application.dto.ProductRequestDTO;
import com.episen.ms_product.application.dto.ProductResponseDTO;
import com.episen.ms_product.application.mapper.ProductMapper;
import com.episen.ms_product.domain.entity.Product;
import com.episen.ms_product.domain.entity.ProductCategory;
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
     * Lister tous les produits : Récupère tous les produits
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
     * Détails d'un prouit : Récupère un produit par son ID
     */
    public ProductResponseDTO getProductById(Long id) {
        log.debug("Récupération du produit avec l'ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        log.info("Produit trouvé: {}", product.getName());

        return productMapper.toDto(product);
    }

    /**
     * Créer un produit : Crée un nouveau produit
     * (Pas besoin de faire +1 au stock car on créé un modle de produit : stock est à entrer par l'utilisateur)
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
    
    /**
     * Modifier un produit : Met à jour un produit existant
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        log.debug("Mise à jour du produit avec l'ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Vérifier si le nouveau nom de produit existe déjà (sauf si c'est le même)
        if (!product.getName().equals(productRequestDTO.getName())
                && productRepository.existsByName(productRequestDTO.getName())) {
            log.warn("Tentative de mise à jour avec un nom de produit existant: {}", productRequestDTO.getName());
            throw new ResourceAlreadyExistsException("Product", "name", productRequestDTO.getName());
        }

        productMapper.updateEntityFromDto(productRequestDTO, product);
        Product updatedProduct = productRepository.save(product);

        // Métrique personnalisée
        Counter.builder("products.updated")
                .description("Nombre de produits mis à jour")
                .tag("type", "product")
                .register(meterRegistry)
                .increment();

        log.info("Produit mis à jour avec succès: ID={}, Name={}",
                updatedProduct.getId(), updatedProduct.getName());

        return productMapper.toDto(updatedProduct);
    }

    /**
     * Supprimer un produit
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Suppression du produit avec l'ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);

        // Métrique personnalisée
        Counter.builder("products.deleted")
                .description("Nombre de produits supprimés")
                .tag("type", "product")
                .register(meterRegistry)
                .increment();

        log.info("Produit supprimé avec succès: ID={}, Name={}", id, product.getName());
    }

    /**
     * Recherche des produits par nom
     */
    public List<ProductResponseDTO> searchProductByName(String name) {
        log.debug("Recherche de produit avec le nom: {}", name);

        List<Product> products = productRepository.searchByName(name);

        log.info("Nombre de produits trouvés: {}", products.size());

        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Filtrer par catégorie : Recherche des produits par catégorie
     */
    public List<ProductResponseDTO> filterProductByCategory(ProductCategory category) {
        log.debug("Recherche de produit avec la catégorie: {}", category);

        List<Product> products = productRepository.findByCategory(category);

        log.info("Nombre de produits trouvés: {}", products.size());

        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Recherche des produits en stock (stock > 0)
     */
    public List<ProductResponseDTO> searchAvailableProducts() {
        log.debug("Recherche de produits en stock");

        List<Product> products = productRepository.findByStockGreaterThan(0);

        log.info("Nombre de produits trouvés: {}", products.size());

        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour le stock d'un produit
     */
        @Transactional
        public ProductResponseDTO updateStock(Long id, Integer newStock) {
                log.debug("Modification du stock du produit {}", id);

                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

                product.setStock(newStock);
                Product savedProduct = productRepository.save(product);

                return productMapper.toDto(savedProduct);
        }
}
