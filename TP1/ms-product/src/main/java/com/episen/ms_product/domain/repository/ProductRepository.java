package com.episen.ms_product.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.episen.ms_product.domain.entity.Product;
import com.episen.ms_product.domain.entity.ProductCategory;

/**
 * Repository pour l'entité Product.
 * Best practices :
 * - Utilisation de Spring Data JPA pour réduire le code boilerplate
 * - Méthodes de requête dérivées pour une meilleure lisibilité
 * - Queries personnalisées avec @Query si nécessaire
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Recherche un produit par id (méthode de requête dérivée)
     */
    Optional<Product> findById(Long id);

    /**
     * Recherche un produit par nom (méthode de requête dérivée)
     */
    Optional<Product> findByName(String name);
    //Trop strict pour une recherche, créer une requete en plus pour la recherche.

    /**
     * Recherche des utilisateurs par nom (insensible à la casse)
     */
    @Query("SELECT u FROM Product u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> searchByName(String name);

    /**
     * Filtrer les produits par catégorie (méthode de re quête dérivée)
     */
    List<Product> findByCategory(ProductCategory categorie);

    /**
     * Filtrer les produits par disponibilité (stock > 0) (méthode de re quête dérivée)
     */
    List<Product> findByStockGreaterThan(int stock);

    /**
     * Vérifie si un nom de produit existe déjà
     */
    boolean existsByName(String name);

    long countByStock(int stock);

    long countByStockLessThan(int threshold);

    long countByActiveTrue();

}
