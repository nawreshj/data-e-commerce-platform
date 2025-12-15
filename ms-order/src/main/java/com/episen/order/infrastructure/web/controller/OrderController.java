package com.episen.order.infrastructure.web.controller;

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

import com.episen.order.application.dto.OrderRequestDto;
import com.episen.order.application.dto.OrderResponseDto;
import com.episen.order.application.dto.UpdateOrderStatusRequestDto;
import com.episen.order.application.service.OrderService;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des commandes.
 *
 * Best practices REST :
 * - Utilisation correcte des verbes HTTP (GET, POST, PATCH, DELETE)
 * - Codes de statut HTTP appropriés (200, 201, 204, 400, 404, etc.)
 * - URI RESTful (/api/v1/orders, /api/v1/orders/{id})
 * - Content negotiation avec MediaType
 * - Documentation OpenAPI/Swagger
 * - Validation des données avec @Valid
 * - ResponseEntity pour un contrôle total de la réponse
 * - Location header pour les ressources créées
 * - Séparation des préoccupations (délégation au service)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API de gestion des commandes")
public class OrderController {

    private final OrderService orderService;

    /**
     * GET /api/v1/orders
     * Récupère la liste de toutes les commandes
     *
     * @return Liste des commandes avec code 200 OK
     */
    @Operation(
            summary = "Récupérer toutes les commandes",
            description = "Retourne la liste complète de toutes les commandes enregistrées"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class)
                    )
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {

        log.info("GET /api/v1/orders - Récupération de toutes les commandes");

        List<OrderResponseDto> orders = orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/v1/orders/{id}
     * Récupère une commande par son ID
     *
     * @param id L'identifiant de la commande
     * @return La commande avec code 200 OK ou 404 NOT FOUND
     */
    @Operation(
            summary = "Récupérer une commande par ID",
            description = "Retourne une commande spécifique basée sur son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Commande trouvée",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée",
                    content = @Content
            )
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDto> getOrderById(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long id) {

        log.info("GET /api/v1/orders/{} - Récupération de la commande", id);

        OrderResponseDto order = orderService.getOrderById(id);

        return ResponseEntity.ok(order);
    }

    /**
     * POST /api/v1/orders
     * Crée une nouvelle commande
     *
     * @param request Données de la commande à créer
     * @return La commande créée avec code 201 CREATED et Location header
     */
    @Operation(
            summary = "Créer une nouvelle commande",
            description = "Crée une commande pour un utilisateur donné avec une liste d'articles. "
                    + "Valide l'existence de l'utilisateur et des produits, calcule le total, "
                    + "et retourne la commande créée."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Commande créée avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides",
                    content = @Content
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDto> createOrder(
            @Parameter(description = "Données de la commande à créer", required = true)
            @Valid @RequestBody OrderRequestDto request) {

        log.info("POST /api/v1/orders - Création d'une commande pour userId={}, nbItems={}",
                request.getUserId(),
                (request.getItems() != null ? request.getItems().size() : 0)
        );

        OrderResponseDto createdOrder = orderService.createOrder(request);

        // Best practice REST : retourner l'URI de la ressource créée dans le header Location
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdOrder);
    }

    /**
     * GET /api/v1/orders/user/{userId}
     * Récupère toutes les commandes d'un utilisateur
     *
     * @param userId L'identifiant de l'utilisateur
     * @return Liste des commandes de l'utilisateur avec code 200 OK
     */
    @Operation(
            summary = "Récupérer les commandes d'un utilisateur",
            description = "Retourne la liste des commandes associées à un utilisateur donné"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class)
                    )
            )
    })
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable Long userId) {

        log.info("GET /api/v1/orders/user/{} - Récupération des commandes utilisateur", userId);

        List<OrderResponseDto> orders = orderService.getOrdersByUser(userId);

        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/v1/orders/status/{status}
     * Récupère toutes les commandes par statut
     *
     * Exemple : /api/v1/orders/status/PENDING
     *
     * @param status Le statut de commande
     * @return Liste des commandes avec ce statut
     */
    @Operation(
            summary = "Récupérer les commandes par statut",
            description = "Retourne la liste des commandes filtrées par statut"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Statut invalide",
                    content = @Content
            )
    })
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(
            @Parameter(description = "Statut de la commande", required = true)
            @PathVariable String status) {

        log.info("GET /api/v1/orders/status/{} - Récupération des commandes par statut", status);

        List<OrderResponseDto> orders = orderService.getOrdersByStatus(status);

        return ResponseEntity.ok(orders);
    }

    /**
     * PATCH /api/v1/orders/{id}/status
     * Met à jour le statut d'une commande existante
     *
     * @param id L'identifiant de la commande
     * @param request Nouveau statut à appliquer
     * @return La commande mise à jour avec code 200 OK
     */
    @Operation(
            summary = "Mettre à jour le statut d'une commande",
            description = "Met à jour le statut d'une commande existante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Commande mise à jour avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée",
                    content = @Content
            )
    })
    @PatchMapping(value = "/{id}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true)
            @Valid @RequestBody UpdateOrderStatusRequestDto request) {

        log.info("PATCH /api/v1/orders/{}/status - Nouveau statut={}", id, request.getStatus());

        OrderResponseDto updated = orderService.updateOrderStatus(id, request);

        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/v1/orders/{id}
     * Supprime une commande
     *
     * @param id L'identifiant de la commande
     * @return Code 204 NO CONTENT
     */
    @Operation(
            summary = "Supprimer une commande",
            description = "Supprime définitivement une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Commande supprimée avec succès",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commande non trouvée",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long id) {

        log.info("DELETE /api/v1/orders/{} - Suppression de la commande", id);

        orderService.deleteOrder(id);

        // Best practice REST : 204 No Content pour une suppression réussie
        return ResponseEntity.noContent().build();
    }
}