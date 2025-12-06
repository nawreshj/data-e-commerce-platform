# TP1  - Plateforme E-Commerce Microservices

## Contexte du Projet

Vous êtes une équipe de développement chargée de créer une **plateforme e-commerce simplifiée** composée de plusieurs microservices. Le projet actuel (`ms-membership`) représente le **Service Membership** déjà implémenté. Vous devez maintenant compléter la plateforme avec deux nouveaux microservices.

**Durée estimée** : 2 semaines, max le lundi 15/12 à minuit    
**Travail** : En binôme ou trinôme  
**Email-Receiver**: rkarra.okad@gmail.com
---

## Architecture Globale de la Plateforme
![alt text](image.png)
---

## Microservices à Développer

### (1️) Service MEMBERSHIP (Déjà fourni)
- Gestion des utilisateurs  
- CRUD complet  
- Validation, exceptions, health checks  
- Métriques Prometheus  

**Port** : 8081

---

### (2️) Service PRODUCT (À créer) 

**Responsabilité** : Gestion du catalogue de produits

#### Modèle de données

**Entité Product** :
```java
- id: Long
- name: String (obligatoire, 3-100 caractères)
- description: String (obligatoire, 10-500 caractères)
- price: BigDecimal (obligatoire, > 0)
- stock: Integer (obligatoire, >= 0)
- category: String (obligatoire: ELECTRONICS, BOOKS, FOOD, OTHER)
- imageUrl: String (optionnel)
- active: Boolean (défaut: true)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

#### Endpoints REST à implémenter

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/v1/products` | Liste tous les produits |
| GET | `/api/v1/products/{id}` | Détail d'un produit |
| POST | `/api/v1/products` | Créer un produit |
| PUT | `/api/v1/products/{id}` | Modifier un produit |
| DELETE | `/api/v1/products/{id}` | Supprimer un produit |
| GET | `/api/v1/products/search?name={name}` | Recherche par nom |
| GET | `/api/v1/products/category/{category}` | Filtrer par catégorie |
| GET | `/api/v1/products/available` | Produits en stock |
| PATCH | `/api/v1/products/{id}/stock` | Mettre à jour le stock |

#### Fonctionnalités spécifiques

- **Validation** : Prix > 0, stock >= 0, catégorie valide
- **Business Rules** :
  - Un produit ne peut pas être supprimé s'il est dans une commande
  - Le stock ne peut pas être négatif
  - Prix doit avoir maximum 2 décimales
- **Health Check personnalisé** : Vérifier le nombre de produits en stock bas (< 5)
- **Métrique personnalisée** : Compteur de produits créés par catégorie

**Port** : 8082

---

### (3️) Service ORDER (À créer) 

**Responsabilité** : Gestion des commandes

#### Modèles de données

**Entité Order** :
```java
- id: Long
- userId: Long (référence vers User)
- orderDate: LocalDateTime
- status: OrderStatus (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- totalAmount: BigDecimal
- shippingAddress: String (obligatoire)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

**Entité OrderItem** :
```java
- id: Long
- orderId: Long
- productId: Long (référence vers Product)
- productName: String (copie du nom au moment de la commande)
- quantity: Integer (> 0)
- unitPrice: BigDecimal
- subtotal: BigDecimal (quantity * unitPrice)
```

**Relation** : Une commande contient plusieurs OrderItems (OneToMany)

#### Endpoints REST à implémenter

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/v1/orders` | Liste toutes les commandes |
| GET | `/api/v1/orders/{id}` | Détail d'une commande |
| POST | `/api/v1/orders` | Créer une commande |
| PUT | `/api/v1/orders/{id}/status` | Changer le statut |
| DELETE | `/api/v1/orders/{id}` | Annuler une commande |
| GET | `/api/v1/orders/user/{userId}` | Commandes d'un utilisateur |
| GET | `/api/v1/orders/status/{status}` | Filtrer par statut |

#### Fonctionnalités spécifiques

- **Validation** :
  - Vérifier que l'utilisateur existe (appel au Service User)
  - Vérifier que les produits existent et sont en stock (appel au Service Product)
  - Calculer automatiquement le totalAmount
- **Business Rules** :
  - Une commande DELIVERED ou CANCELLED ne peut plus être modifiée
  - À la création, déduire les quantités du stock des produits
  - Une commande doit contenir au moins un article
- **Communication inter-services** :
  - Utiliser RestTemplate ou WebClient pour appeler User et Product
  - Gérer les erreurs si un service est down
- **Health Check personnalisé** : Vérifier la disponibilité des services User et Product
- **Métrique personnalisée** : 
  - Compteur de commandes par statut
  - Gauge du montant total des commandes du jour

**Port** : 8083

---

## Livrables Attendus

### 1. Code source
Pour **chaque microservice** (Product et Order) :

```
service-product/
├── pom.xml
├── README.md (spécifique au service)
├── src/main/java/
│   └── com/example/product/
│       ├── ProductApplication.java
│       ├── application/
│       │   ├── dto/
│       │   ├── mapper/
│       │   └── service/
│       ├── domain/
│       │   ├── entity/
│       │   └── repository/
│       └── infrastructure/
│           ├── web/controller/
│           ├── exception/
│           ├── health/
│           └── client/ (pour appels inter-services)
├── src/main/resources/
│   ├── application.yml
│   └── data.sql
└── src/test/java/
```

**Exigences** :
- Architecture en couches respectée (comme le service User fourni)
- DTOs Request/Response séparés
- Validation Bean Validation
- Gestion d'exceptions avec @ControllerAdvice
- Health checks personnalisés
- Métriques Prometheus custom
- Documentation OpenAPI/Swagger
- Au moins 3 tests unitaires par microservice
- Code commenté et propre

---

### 2. Documentation Architecture

Créer un dossier `architecture/` contenant :

#### 2.1 Document d'Architecture Technique (DAT)
**Fichier** : `architecture/DAT.md  ou DAT.docx`

**Contenu attendu** :
- Vue d'ensemble de la plateforme
- Schéma d'architecture (diagramme)
- Description de chaque microservice
- Choix technologiques justifiés
- Stratégie de communication inter-services
- Gestion des données (base de données par service)
- Gestion des erreurs et résilience

### 2.2. Guide de Déploiement

**Fichier** : `DEPLOYMENT.md`

**Contenu attendu** :
- Prérequis (Java, Maven, Docker, etc.)
- Instructions de démarrage pas-à-pas :
  1. Cloner le repository
  2. Compiler chaque service
  3. Lancer dans le bon ordre
  4. Vérifier que tout fonctionne
- Configuration des ports
- Variables d'environnement
- Troubleshooting courant

---

### 3. Tests et Validation

#### Collection Postman
**Fichier** : `postman/platform-tests.json`

**Scénarios de test** :
1. **Scénario complet** : Créer user → Créer produits → Créer commande
2. **Tests d'erreur** : Commande avec user inexistant, produit en rupture, etc.
3. **Tests de chaque endpoint** : Happy path

### 4. Configuration Monitoring  (Optionnel)

**Fichier** : `docker-compose.yml` (global pour toute la plateforme)

**Services à configurer** :
- Prometheus (scraping des 3 microservices)
- Grafana (avec datasource Prometheus préconfigurée)

**Fichiers supplémentaires** :
- `prometheus.yml` : Configuration scraping
- `monitoring/MONITORING.md` : Guide d'utilisation du monitoring

**Dashboards Grafana** (optionnel mais bonus) :
- Dashboard général : Vue d'ensemble des 3 services
- Dashboard métier : Commandes du jour, produits en rupture, etc.

---

## Questions Fréquentes

### Q1 : Peut-on utiliser une vraie base de données (PostgreSQL) ?
**R** : Oui, c'est même un bonus ! Mais H2 est suffisant pour ce TP.

### Q2 : Doit-on implémenter l'authentification ?
**R** : Non, ce n'est pas obligatoire pour ce TP. Concentrez-vous sur les fonctionnalités métier.

### Q3 : Les microservices doivent-ils communiquer en temps réel ?
**R** : Non, des appels REST synchrones avec RestTemplate suffisent.

### Q4 : Combien de produits initiaux doit-on créer ?
**R** : Au moins 5-10 produits de différentes catégories dans `data.sql`.

### Q5 : Faut-il gérer les transactions distribuées ?
**R** : Non, une gestion simple des erreurs suffit. Si un service échoue, retourner une erreur appropriée.

---

##  Soutenance Finale, démonstration de 10 minutes par groupe
1. Présentation rapide de l'architecture (4 min)
2. Démonstration du scénario complet (4 min)
   - Créer utilisateur, produits, commande
   - Montrer les health checks
   - Montrer les métriques dans Prometheus
   - Montrer un dashboard Grafana
3. Démonstration de la gestion d'erreur (2 min)
   - Commande avec user inexistant
   - Commande avec produit en rupture

---
