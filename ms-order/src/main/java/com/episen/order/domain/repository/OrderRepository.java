package com.episen.order.domain.repository;


import com.episen.order.domain.entity.Order;
import com.episen.order.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Récupère toutes les commandes d'un utilisateur.
     */
    List<Order> findByUserId(Long userId);

    /**
     * Récupère toutes les commandes avec un statut donné.
     */
    List<Order> findByStatus(OrderStatus status);

   
     /**
     *  fais la somme des 
     */



    /**
     * Calcule le montant total des commandes sur une période donnée.
     *
     * Cette méthode est utilisée pour alimenter une métrique de type Gauge
     * (ex : montant total des commandes du jour).
     *
     * Fonctionnement :
     * - On filtre les commandes dont la date de création est comprise
     *   entre deux bornes temporelles (start inclus, end exclu)
     * - On fait la somme du champ totalAmount côté base de données
     * - COALESCE permet d'éviter un retour null si aucune commande n'existe
     *
     * @param start date/heure de début de la période
     * @param end   date/heure de fin de la période
     * @return somme des montants des commandes sur la période
     */
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.createdAt >= :start
        AND o.createdAt < :end
    """)
    Double sumTotalAmountBetween(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

}