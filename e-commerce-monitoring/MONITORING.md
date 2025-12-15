# Guide d'utilisation du monitoring


1. Objectif

Ce document décrit les principes et l’utilisation du système de monitoring
mis en place pour les microservices de la plateforme e-commerce.

2. Architecture du monitoring

Le monitoring repose sur :
- Spring Boot Actuator pour l’exposition des métriques applicatives
- Micrometer avec export Prometheus
- Prometheus pour la collecte des métriques
- Grafana pour la visualisation

3. Accès aux outils

- Prometheus
        http://localhost:8081/actuator/prometheus
        http://localhost:8082/actuator/prometheus
        http://localhost:8083/actuator/prometheus
- Grafana
        http://localhost:3000/dashboards

4. Métriques principales surveillées

Trafic applicatif – RPS
Permet de mesurer la charge instantanée par service.

Latence p95
Mesure les temps de réponse élevés affectant l’expérience utilisateur.

Commande Total par jour
Permet de mesurer le nombre total de commande de la journée

Changement de statut de la commande
Mesure qui permet de voir le nombre de changement (pending/confirmed...) de la journée

