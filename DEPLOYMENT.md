## Guide de Déploiement

### 1. Prérequis

- Java 17+ (ou 21)
- Maven 3.8+
- Git
- Docker & Docker Compose
- Prometheus & Grafana pour le monitoring

### 2. Cloner le repository

```bash
git clone <https://github.com/nawelGl/data-e-commerce-platform.git>
cd data-e-commerce-platform
```

### 3. Compiler chaque service

```bash
cd ms-membership
mvn clean package

cd ../ms-product
mvn clean package

cd ../ms-order
mvn clean package
```

### 4. Lancer dans le bon ordre

Ordre conseillé (dépendances de ms-order) :
1) **ms-user**
2) **ms-product**
3) **ms-order**

Exemple :

```bash
# ms-user
java -jar target/ms-membership*.jar

# ms-product
java -jar target/ms-product*.jar

# ms-order
java -jar target/ms-order*.jar
```

### 5. Ports (par défaut)

| Microservice | Port |
|-------------|------|
| ms-order | 8080 |
| ms-user | 8081 |
| ms-product | 8082 |

### 6. Variables de configuration

Exemple (ms-order `application.yml`) :

```yaml
app:
  clients:
    user:
      base-url: http://localhost:8081
      actuator-url: http://localhost:8081/actuator/health
    product:
      base-url: http://localhost:8082
      actuator-url: http://localhost:8082/actuator/health
```

### 7. Vérifier que tout fonctionne

Health checks :

```http
GET http://localhost:8081/actuator/health
GET http://localhost:8082/actuator/health
GET http://localhost:8080/actuator/health
```

Prometheus metrics :

```http
GET http://localhost:8080/actuator/prometheus
```

### 8. Troubleshooting courant

- **Service DOWN dans /actuator/health (ms-order)** :
  - vérifier que ms-user et ms-product tournent
  - vérifier les URLs/ports dans `application.yml`
- **Erreur lors de la création d’une commande** :
  - utilisateur introuvable / produit introuvable
  - stock insuffisant
  - service externe indisponible
- **Métriques absentes** :
  - vérifier les dépendances Actuator + micrometer-registry-prometheus
  - vérifier `management.endpoints.web.exposure.include` contient `prometheus`
