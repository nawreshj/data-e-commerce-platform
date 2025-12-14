## Document d’Architecture Technique (DAT)

### 1. Vue d’ensemble de la plateforme

La plateforme est une application e-commerce basée sur une **architecture microservices**, conçue pour séparer les responsabilités fonctionnelles, améliorer la maintenabilité et permettre une évolution indépendante de chaque composant.

Microservices :
- **ms-membership (user)** : gestion des utilisateurs
- **ms-product** : gestion du catalogue et des stocks
- **ms-order** : gestion des commandes
- **Monitoring** : Prometheus & Grafana (via Actuator + Micrometer)

### 2. Schéma d’architecture

Voir le fichier : `architecture/architecture.png`

### 3. Description des microservices

#### 3.1 ms-membership (user)
- Gestion des utilisateurs (CRUD / consultation)
- Expose des endpoints REST
- Base de données dédiée : Users

#### 3.2 ms-product
- Gestion du catalogue produits
- Gestion du stock (lecture / mise à jour après commande)
- Base de données dédiée : Products

#### 3.3 ms-order
- Création, consultation et modification des commandes
- Appels REST vers ms-user et ms-product
- Calcul du montant total, règles métier (statuts non modifiables, etc.)
- Base de données dédiée : Orders

### 4. Choix technologiques justifiés

- **Spring Boot** : framework standard et productif pour microservices
- **Maven** : gestion des dépendances et packaging reproductible
- **JPA/Hibernate** : persistance ORM (entités + repositories)
- **REST HTTP** : communication inter-services simple et interopérable
- **Spring Boot Actuator** : health checks & endpoints de diagnostic
- **Micrometer + Prometheus registry** : métriques exportables (/actuator/prometheus)
- **Prometheus + Grafana** : collecte et visualisation des métriques

### 5. Stratégie de communication inter-services

Communication **synchrone REST** :
- `ms-order → ms-membership` : vérification utilisateur
- `ms-order → ms-product` : vérification produit/stock + mise à jour de stock

Les clients sont isolés dans des classes dédiées (ex: `UserClient`, `ProductClient`) afin de centraliser la logique d’appel HTTP.

### 6. Gestion des données (base par service)

Principe : **Une base de données par microservice**.
- Pas d’accès direct aux bases des autres services
- Échanges uniquement via API REST
- Isolation des responsabilités et meilleure résilience

### 7. Gestion des erreurs et résilience

- Validations métier côté service (inputs, statuts, stock…)
- Gestion d’exceptions (erreurs fonctionnelles vs indisponibilité service externe)
- Health-check global côté `ms-order` via un `HealthIndicator` qui vérifie ms-membership et ms-product
- Exposition des endpoints Actuator :
  - `/actuator/health`
  - `/actuator/metrics`
  - `/actuator/prometheus`

### 8. Monitoring & métriques

Chaque microservice expose :
- **Health** : `/actuator/health`
- **Prometheus** : `/actuator/prometheus`

Exemples de métriques métier (ms-order) :
- compteur de commandes créées par statut
- compteur de changements de statut
- gauge du montant total des commandes du jour
