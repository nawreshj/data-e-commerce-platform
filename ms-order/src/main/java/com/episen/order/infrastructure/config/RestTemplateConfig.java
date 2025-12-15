package com.episen.order.infrastructure.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * Configuration du RestTemplate utilisé par les clients REST (UserClient / ProductClient).
 *
 * Objectif :
 *  - fournir un RestTemplate capable de supporter toutes les méthodes HTTP,
 *    notamment PATCH, grâce à HttpComponentsClientHttpRequestFactory.
 *  - centraliser la configuration pour ne pas dupliquer dans les services.
 */

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        // Factory HTTP basée sur Apache HttpClient → nécessaire pour les requêtes PATCH
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(
                        HttpClients.createDefault()
                );

        return builder
                .requestFactory(() -> requestFactory)
                .build();
    }
}