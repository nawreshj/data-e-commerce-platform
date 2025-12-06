package com.episen.ms_product.infrastructure.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.episen.ms_product.application.dto.ProductRequestDTO;
import com.episen.ms_product.application.dto.ProductResponseDTO;
import com.episen.ms_product.application.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
}
