package com.episen.order.infrastructure.client;

import com.episen.order.application.dto.ProductDto;
import com.episen.order.application.dto.StockUpdateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;



/**
 * Client REST dédié à la communication avec le microservice ms-product.
 *
 * Rôles :
 *  - récupérer un produit par son identifiant (vérification d’existence, prix, stock) ;
 *  - mettre à jour le stock d’un produit lors de la création d'une commande.
 *
 * Particularités :
 *  - n’expose aucune logique métier : simple façade réseau ;
 *  - l’URL de base est injectée via application.yml pour respecter les bonnes pratiques ;
 *  - gère et remonte proprement les erreurs réseau (RestClientException).
 *
 * Ce client permet d’isoler toutes les interactions HTTP, afin de garder
 * un service métier (OrderService) propre et indépendant du transport.
 */

@Slf4j
@Component
public class ProductClient {

    private final RestTemplate restTemplate;
    private final String productBaseUrl;

    public ProductClient(RestTemplate restTemplate,
                         @Value("${app.clients.product.base-url}") String productBaseUrl) {
        this.restTemplate = restTemplate;
        this.productBaseUrl = productBaseUrl;
    }

    // GET /api/v1/products/{id}
    public ProductDto getProductById(Long productId) {
        String url = productBaseUrl + "/api/v1/products/" + productId;
        log.info("GET {} - Récupération produit {}", url, productId);
        return restTemplate.getForObject(url, ProductDto.class);
    }

    // PATCH /api/v1/products/{id}/stock
    public void updateProductStock(Long productId, int newStock) {
        String url = productBaseUrl + "/api/v1/products/" + productId + "/stock";

        StockUpdateRequestDto body = StockUpdateRequestDto.builder()
                .newStock(newStock)
                .build();

        log.info("PATCH {} - Mise à jour stock produit {} -> {}", url, productId, newStock);

        try {
            restTemplate.patchForObject(url, body, Void.class);
        } catch (RestClientException ex) {
            log.error("Erreur lors de la mise à jour du stock pour productId={}", productId, ex);
            throw new IllegalStateException(
                    "Impossible de mettre à jour le stock du produit (service Product indisponible).",
                    ex
            );
        }
    }
}