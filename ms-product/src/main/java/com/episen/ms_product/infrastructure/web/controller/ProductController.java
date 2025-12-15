package com.episen.ms_product.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.episen.ms_product.application.dto.ProductRequestDTO;
import com.episen.ms_product.application.dto.ProductResponseDTO;
import com.episen.ms_product.application.dto.StockUpdateDTO;
import com.episen.ms_product.application.service.ProductService;
import com.episen.ms_product.domain.entity.Product;
import com.episen.ms_product.domain.entity.ProductCategory;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des produits.
 * 
 * Best practices REST :
 * - Utilisation correcte des verbes HTTP (GET, POST, PUT, DELETE, PATCH)
 * - Codes de statut HTTP appropriés (200, 201, 204, 404, etc.)
 * - URI RESTful (/api/v1/products, /api/v1/products/{id})
 * - Content negotiation avec MediaType
 * - Documentation OpenAPI/Swagger
 * - Validation des données avec @Valid
 * - ResponseEntity pour un contrôle total de la réponse
 * - Location header pour les ressources créées
 * - Séparation des préoccupations (délégation au service)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API de gestion des produits")
public class ProductController {
    
    private final ProductService productService;

    /**
     * GET /api/v1/products
     * Récupère la liste de tous les produits
     * 
     * @return Liste des produits avec code 200 OK
     */
    @Operation(summary = "Récupérer tous les produits", 
               description = "Retourne la liste complète de tous les produits enregistrés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = ProductResponseDTO.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        log.info("GET /api/v1/products - Récupération de tous les produits");
        
        List<ProductResponseDTO> products = productService.getAllProducts();
        
        return ResponseEntity.ok(products);
    }

     /**
     * GET /api/v1/products/{id}
     * Récupère un produit par son ID
     * 
     * @param id L'identifiant du produit
     * @return Le produit avec code 200 OK ou 404 NOT FOUND
     */
    @Operation(summary = "Récupérer un produit par ID", 
               description = "Retourne un produit spécifique basé sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produit trouvé",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé",
                    content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id) {
        
        log.info("GET /api/v1/products/{} - Récupération du produit", id);
        
        ProductResponseDTO product = productService.getProductById(id);
        
        return ResponseEntity.ok(product);
    }

    /**
     * POST /api/v1/products
     * Crée un nouveau produit
     * 
     * @param productRequestDTO Les données du produit à créer
     * @return Le produit créé avec code 201 CREATED et Location header
     */
    @Operation(summary = "Créer un nouveau produit", 
               description = "Crée un nouveau produit avec les données fournies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produit créé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content),
        @ApiResponse(responseCode = "409", description = "Le produit existe déjà",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Parameter(description = "Données du produit à créer", required = true)
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        
        log.info("POST /api/v1/products - Création d'un produit : {}", productRequestDTO.getName());
        
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        
        // Best practice REST : retourner l'URI de la ressource créée dans le header Location
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();
        
        return ResponseEntity
                .created(location)
                .body(createdProduct);
    }


    /**
     * PUT /api/v1/products/{id}
     * Met à jour complètement un produit existant
     * 
     * @param id             L'identifiant du produit
     * @param productRequestDTO Les nouvelles données du produit
     * @return Le produit mis à jour avec code 200 OK
     */
    @Operation(summary = "Mettre à jour un produit", description = "Met à jour complètement les informations d'un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflit avec un produit existant", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du produit", required = true)
            @Valid
            @RequestBody ProductRequestDTO productRequestDTO) {

        log.info("PUT /api/v1/products/{} - Mise à jour du produit", id);

        ProductResponseDTO updateProduct = productService.updateProduct(id, productRequestDTO);

        return ResponseEntity.ok(updateProduct);
    }

    /**
     * DELETE /api/v1/products/{id}
     * Supprime un produit
     * 
     * @param id L'identifiant du produit
     * @return Code 204 NO CONTENT
     */
    @Operation(summary = "Supprimer un produit", description = "Supprime définitivement un produit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID du produit", required = true) @PathVariable Long id) {

        log.info("DELETE /api/v1/products/{} - Suppression du produit", id);

        productService.deleteProduct(id);

        // Best practice REST : 204 No Content pour une suppression réussie
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/products/search?name={name}
     * Recherche des produits par nom
     * 
     * @param name Le nom à rechercher
     * @return Liste des produits correspondants
     */
    @Operation(summary = "Rechercher des produits par nom", description = "Recherche des produits dont le nom contient la chaîne spécifiée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponseDTO.class)))
    })
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @Parameter(description = "Nom du produit à rechercher", required = true) @RequestParam String name) {

        log.info("GET /api/v1/products/search?name={} - Recherche de produits", name);

        List<ProductResponseDTO> products = productService.searchProductByName(name);

        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/v1/products/category/{category}
     * Recherche des produits par catégorie
     * 
     * @param category La catégorie à rechercher
     * @return Liste des produits correspondants
     */
    @Operation(summary = "Rechercher des produits par catégorie", description = "Recherche des produits dont la catégorie équivaut à la chaîne spécifiée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponseDTO.class)))
    })
    @GetMapping(value = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByCategory(
            @Parameter(description = "Catégorie du produit à rechercher", required = true)
            @PathVariable ProductCategory category) {

        log.info("GET /api/v1/products/category/{category} - Recherche de produits", category);

        List<ProductResponseDTO> products = productService.filterProductByCategory(category);

        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/v1/products/available
     * Recherche des produits en stock
     * 
     * @return Liste des produits en stock
     */
    @Operation(summary = "Rechercher des produits en stock", description = "Recherche des produits en stock (stock > 0)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponseDTO.class)))
    })
    @GetMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDTO>> searchAvailableProducts() {

        log.info("GET /api/v1/products/available - Recherche de produits");

        List<ProductResponseDTO> products = productService.searchAvailableProducts();

        return ResponseEntity.ok(products);
    }

    /**
     * PATCH /api/v1/products/{id}/stock
     * Met à jour le stock d'un produit existant
     * 
     * @param id       L'identifiant du produit
     * @param newStock Les nouveau stock du produit
     * @return Le produit mis à jour avec code 200 OK
     */
    @Operation(summary = "Mettre à jour le stock d'un produit", description = "Met à jour le stock d'un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé", content = @Content),
    })
    @PatchMapping(value = "/{id}/stock", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> updateProductStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO stockUpdateDTO) {

        log.info("PATCH /api/v1/products/{}/stock - Nouveau stock : {}", id, stockUpdateDTO.getNewStock());

        ProductResponseDTO updatedProduct = productService.updateStock(id, stockUpdateDTO.getNewStock());

        return ResponseEntity.ok(updatedProduct);
    }
}
