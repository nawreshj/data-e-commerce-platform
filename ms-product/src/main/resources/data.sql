-- Données initiales pour la base de données H2
-- Ce script est exécuté automatiquement au démarrage de l'application
-- Création de la table products
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    image_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- 2. Insertion des données de test
-- On force les ID pour être sûr de les retrouver facilement avec Postman

-- ELECTRONICS
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (1, 'iPhone 15', 'Smartphone dernière génération avec écran Super Retina.', 999.99, 50, 'ELECTRONICS', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (2, 'Sony WH-1000XM5', 'Casque sans fil à réduction de bruit active.', 349.00, 15, 'ELECTRONICS', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (3, 'Samsung Monitor 27"', 'Ecran PC 4K UHD parfait pour le développement.', 299.50, 8, 'ELECTRONICS', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

-- BOOKS
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (4, 'Clean Code', 'Livre de référence de Robert C. Martin pour les développeurs.', 35.50, 100, 'BOOKS', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (5, 'Le Seigneur des Anneaux', 'L''intégrale de la trilogie de J.R.R. Tolkien.', 25.00, 30, 'BOOKS', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (6, 'Spring Boot in Action', 'Guide complet pour maîtriser le framework Java.', 45.00, 20, 'BOOKS', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

-- FOOD
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (7, 'Café en grains 1kg', 'Mélange Arabica/Robusta intense.', 15.99, 200, 'FOOD', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (8, 'Chocolat Noir 85%', 'Tablette de chocolat bio équitable.', 2.50, 500, 'FOOD', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (9, 'Pâtes Italiennes', 'Paquet de 500g de Penne Rigate.', 1.20, 0, 'FOOD', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP); 
-- NOTE: Stock à 0 pour tester le filtre "available"

-- OTHER
INSERT INTO products (id, name, description, price, stock, category, image_url, active, created_at) 
VALUES (10, 'Chaise de Bureau', 'Chaise ergonomique avec support lombaire.', 149.99, 5, 'OTHER', 'https://via.placeholder.com/150', true, CURRENT_TIMESTAMP);

-- Commande nécessaire pour H2 si on insère des ID manuellement :
-- On dit à la séquence de redémarrer à 11, sinon le prochain insert plantera (Primary Key violation)
ALTER TABLE products ALTER COLUMN id RESTART WITH 11;