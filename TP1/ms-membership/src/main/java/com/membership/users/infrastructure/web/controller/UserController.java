package com.membership.users.infrastructure.web.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.membership.users.application.dto.UserRequestDTO;
import com.membership.users.application.dto.UserResponseDTO;
import com.membership.users.application.service.UserService;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des utilisateurs.
 * 
 * Best practices REST :
 * - Utilisation correcte des verbes HTTP (GET, POST, PUT, DELETE, PATCH)
 * - Codes de statut HTTP appropriés (200, 201, 204, 404, etc.)
 * - URI RESTful (/api/v1/users, /api/v1/users/{id})
 * - Content negotiation avec MediaType
 * - Documentation OpenAPI/Swagger
 * - Validation des données avec @Valid
 * - ResponseEntity pour un contrôle total de la réponse
 * - Location header pour les ressources créées
 * - Séparation des préoccupations (délégation au service)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API de gestion des utilisateurs")
public class UserController {

    private final UserService userService;
    /**
     * GET /api/v1/users
     * Récupère la liste de tous les utilisateurs
     * 
     * @return Liste des utilisateurs avec code 200 OK
     */
    @Operation(summary = "Récupérer tous les utilisateurs", 
               description = "Retourne la liste complète de tous les utilisateurs enregistrés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = UserResponseDTO.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/v1/users - Récupération de tous les utilisateurs");
        
        List<UserResponseDTO> users = userService.getAllUsers();
        
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/{id}
     * Récupère un utilisateur par son ID
     * 
     * @param id L'identifiant de l'utilisateur
     * @return L'utilisateur avec code 200 OK ou 404 NOT FOUND
     */
    @Operation(summary = "Récupérer un utilisateur par ID", 
               description = "Retourne un utilisateur spécifique basé sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable Long id) {
        
        log.info("GET /api/v1/users/{} - Récupération de l'utilisateur", id);
        
        UserResponseDTO user = userService.getUserById(id);
        
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/v1/users
     * Crée un nouvel utilisateur
     * 
     * @param userRequestDTO Les données de l'utilisateur à créer
     * @return L'utilisateur créé avec code 201 CREATED et Location header
     */
    @Operation(summary = "Créer un nouvel utilisateur", 
               description = "Crée un nouvel utilisateur avec les données fournies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content),
        @ApiResponse(responseCode = "409", description = "L'utilisateur existe déjà",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "Données de l'utilisateur à créer", required = true)
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        
        log.info("POST /api/v1/users - Création d'un utilisateur: {}", userRequestDTO.getEmail());
        
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        
        // Best practice REST : retourner l'URI de la ressource créée dans le header Location
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        
        return ResponseEntity
                .created(location)
                .body(createdUser);
    }

    /**
     * PUT /api/v1/users/{id}
     * Met à jour complètement un utilisateur existant
     * 
     * @param id L'identifiant de l'utilisateur
     * @param userRequestDTO Les nouvelles données de l'utilisateur
     * @return L'utilisateur mis à jour avec code 200 OK
     */
    @Operation(summary = "Mettre à jour un utilisateur", 
               description = "Met à jour complètement les informations d'un utilisateur existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflit avec un utilisateur existant",
                    content = @Content)
    })
    @PutMapping(value = "/{id}", 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'utilisateur", required = true)
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        
        log.info("PUT /api/v1/users/{} - Mise à jour de l'utilisateur", id);
        
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * DELETE /api/v1/users/{id}
     * Supprime un utilisateur
     * 
     * @param id L'identifiant de l'utilisateur
     * @return Code 204 NO CONTENT
     */
    @Operation(summary = "Supprimer un utilisateur", 
               description = "Supprime définitivement un utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès",
                    content = @Content),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable Long id) {
        
        log.info("DELETE /api/v1/users/{} - Suppression de l'utilisateur", id);
        
        userService.deleteUser(id);
        
        // Best practice REST : 204 No Content pour une suppression réussie
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/users/search?lastName={lastName}
     * Recherche des utilisateurs par nom
     * 
     * @param lastName Le nom à rechercher
     * @return Liste des utilisateurs correspondants
     */
    @Operation(summary = "Rechercher des utilisateurs par nom", 
               description = "Recherche des utilisateurs dont le nom contient la chaîne spécifiée")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = UserResponseDTO.class)))
    })
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponseDTO>> searchUsers(
            @Parameter(description = "Nom de famille à rechercher", required = true)
            @RequestParam String lastName) {
        
        log.info("GET /api/v1/users/search?lastName={} - Recherche d'utilisateurs", lastName);
        
        List<UserResponseDTO> users = userService.searchUsersByLastName(lastName);
        
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/active
     * Récupère tous les utilisateurs actifs
     * 
     * @return Liste des utilisateurs actifs
     */
    @Operation(summary = "Récupérer les utilisateurs actifs", 
               description = "Retourne la liste de tous les utilisateurs actifs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = UserResponseDTO.class)))
    })
    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponseDTO>> getActiveUsers() {
        log.info("GET /api/v1/users/active - Récupération des utilisateurs actifs");
        
        List<UserResponseDTO> users = userService.getActiveUsers();
        
        return ResponseEntity.ok(users);
    }

    /**
     * PATCH /api/v1/users/{id}/deactivate
     * Désactive un utilisateur (soft delete)
     * 
     * @param id L'identifiant de l'utilisateur
     * @return L'utilisateur désactivé
     */
    @Operation(summary = "Désactiver un utilisateur", 
               description = "Désactive un utilisateur sans le supprimer définitivement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur désactivé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content)
    })
    @PatchMapping(value = "/{id}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> deactivateUser(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable Long id) {
        
        log.info("PATCH /api/v1/users/{}/deactivate - Désactivation de l'utilisateur", id);
        
        UserResponseDTO deactivatedUser = userService.deactivateUser(id);
        
        return ResponseEntity.ok(deactivatedUser);
    }
}
