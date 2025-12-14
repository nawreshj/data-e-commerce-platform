package com.episen.order.infrastructure.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import com.episen.order.application.dto.UserDto;


/**
 * Client REST responsable de la communication avec le microservice ms-users.
 *
 * Rôles :
 *  - interroger ms-users pour vérifier l'existence d’un utilisateur ;
 *  - isoler la logique réseau hors du service métier (OrderService).
 *
 * Particularités :
 *  - l’URL de base est injectée via application.yml (bonne pratique) ;
 *  - ce client ne contient aucune logique métier, uniquement du transport HTTP.
 */

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userBaseUrl;

    public UserClient(RestTemplate restTemplate,
                      @Value("${app.clients.user.base-url}") String userBaseUrl) {
        this.restTemplate = restTemplate;
        this.userBaseUrl = userBaseUrl;
    }

    // GET /api/v1/users/{id}
    public UserDto getUserById(Long id) {
        String url = userBaseUrl + "/api/v1/users/" + id;
        return restTemplate.getForObject(url, UserDto.class);
    }
}