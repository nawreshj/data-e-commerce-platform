package com.membership.users.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.membership.users.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité User.
 * Best practices :
 * - Utilisation de Spring Data JPA pour réduire le code boilerplate
 * - Méthodes de requête dérivées pour une meilleure lisibilité
 * - Queries personnalisées avec @Query si nécessaire
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par email (méthode de requête dérivée)
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Récupère tous les utilisateurs actifs
     */
    List<User> findByActiveTrue();

    /**
     * Recherche des utilisateurs par nom (insensible à la casse)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<User> searchByLastName(String lastName);

    /**
     * Compte le nombre d'utilisateurs actifs
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();
}
