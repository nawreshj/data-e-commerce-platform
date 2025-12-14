package com.episen.order.application.service;

import com.episen.order.application.dto.OrderItemRequestDto;
import com.episen.order.application.dto.OrderRequestDto;
import com.episen.order.application.dto.OrderResponseDto;
import com.episen.order.application.mapper.OrderItemMapper;
import com.episen.order.application.mapper.OrderMapper;
import com.episen.order.domain.entity.Order;
import com.episen.order.domain.entity.OrderItem;
import com.episen.order.domain.enums.OrderStatus;
import com.episen.order.domain.repository.OrderRepository;
import com.episen.order.infrastructure.client.ProductClient;
import com.episen.order.infrastructure.client.UserClient;
import com.episen.order.application.dto.ProductDto;
import com.episen.order.application.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.episen.order.infrastructure.metrics.OrderMetrics;

import com.episen.order.application.dto.UpdateOrderStatusRequestDto;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderMetrics orderMetrics;

   @Override
    public OrderResponseDto createOrder(OrderRequestDto request) {

        // ─────────────────────────────────────────────
        // 1) VALIDATION DE BASE : au moins un article
        // ─────────────────────────────────────────────
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins un article.");
        }

        // ─────────────────────────────────────────────
        // 2) VALIDATION : vérifier que l'utilisateur existe (ms-user)
        // ─────────────────────────────────────────────
        UserDto user;
        try {
            user = userClient.getUserById(request.getUserId());
        } catch (RestClientException ex) {
            log.error("Erreur lors de l'appel au service User pour userId={}", request.getUserId(), ex);
            throw new IllegalStateException("Impossible de vérifier l'utilisateur (service User indisponible).", ex);
        }

        if (user == null) {
            throw new IllegalArgumentException("Utilisateur introuvable pour id=" + request.getUserId());
        }

        // ─────────────────────────────────────────────
        // 3) CONSTRUCTION DE L’ENTITÉ ORDER (sans items, sans total)
        //    -> mapper "pauvre" + enrichissement dans le service
        // ─────────────────────────────────────────────
        Order order = orderMapper.toEntityFromRequest(request);
        order.setStatus(OrderStatus.PENDING);               // statut initial
        order.setOrderDate(LocalDateTime.now());            // date de commande
        // totalAmount, items seront remplis plus bas

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // ─────────────────────────────────────────────
        // 4) POUR CHAQUE ITEM : vérifier le produit, le stock, calculer le sous-total
        // ─────────────────────────────────────────────
       for (OrderItemRequestDto itemDto : request.getItems()) {

    // 4.1) Récupérer le produit sur ms-product
    ProductDto product;
    try {
        product = productClient.getProductById(itemDto.getProductId());
    } catch (RestClientException ex) {
        log.error("Erreur lors de l'appel au service Product pour productId={}", itemDto.getProductId(), ex);
        throw new IllegalStateException("Impossible de vérifier le produit (service Product indisponible).", ex);
    }

    if (product == null) {
        throw new IllegalArgumentException("Produit introuvable pour id=" + itemDto.getProductId());
    }

    // 4.2) Vérifier le stock disponible
    if (product.getStock() == null || product.getStock() < itemDto.getQuantity()) {
        throw new IllegalArgumentException(
                "Stock insuffisant pour le produit id=" + product.getId()
                        + " (stock=" + product.getStock()
                        + ", demandé=" + itemDto.getQuantity() + ")"
        );
    }

    // 4.3) Mapper le DTO -> entité OrderItem (mapper pauvre)
    OrderItem orderItem = orderItemMapper.toEntity(itemDto);

    // 4.4) Enrichir l’item avec les infos produit + relation vers la commande
    orderItem.setOrder(order);
    orderItem.setProductName(product.getName());
    orderItem.setUnitPrice(product.getPrice());

    // 4.5) Calculer le subtotal : unitPrice * quantity
    BigDecimal lineTotal = product.getPrice()
            .multiply(BigDecimal.valueOf(itemDto.getQuantity()));
    orderItem.setSubtotal(lineTotal);

    // 4.6) Accumuler le total de la commande
    totalAmount = totalAmount.add(lineTotal);

    // 4.7) Ajouter l’item à la liste
    orderItems.add(orderItem);

    // 4.8) BUSINESS RULE : à la création, déduire les quantités du stock
    Integer currentStock = product.getStock();
    int requestedQty = itemDto.getQuantity();
    int newStock = currentStock - requestedQty;

    productClient.updateProductStock(product.getId(), newStock);

    log.debug("Stock mis à jour pour productId={} : {} -> {}",
            product.getId(), currentStock, newStock);
}

        // ─────────────────────────────────────────────
        // 5) Finaliser l’entité Order (items + totalAmount)
        // ─────────────────────────────────────────────
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // ─────────────────────────────────────────────
        // 6) PERSISTENCE : sauvegarder la commande (cascade => sauvegarde aussi les items)
        // ─────────────────────────────────────────────
        Order savedOrder = orderRepository.save(order);

        // métrique : 1 commande créée dans le statut initial
        orderMetrics.incrementOrdersCreated(savedOrder.getStatus());

        // ─────────────────────────────────────────────
        // 7) MAPPING : entité -> DTO de réponse
        // ─────────────────────────────────────────────
        return orderMapper.toDto(savedOrder);
    }

    @Override
public OrderResponseDto getOrderById(Long id) {

    Order order = orderRepository.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException("Commande introuvable pour id=" + id)
            );

    return orderMapper.toDto(order);
}

    @Override
    public List<OrderResponseDto> getAllOrders() {

        // 1) Charger toutes les commandes depuis la base
        List<Order> orders = orderRepository.findAll();

        // 2) Mapper les entités -> DTO de réponse
        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }

   @Override
    public List<OrderResponseDto> getOrdersByUser(Long userId) {

        // (Optionnel) validation user existe via ms-user si tu veux
        // userClient.getUserById(userId);

        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders == null || orders.isEmpty()) {
            return List.of(); //  jamais null
        }

        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getOrdersByStatus(String status) {

        OrderStatus orderStatus;

        // 1) Conversion String -> Enum (avec validation)
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Statut de commande invalide : " + status
            );
        }

        // 2) Récupération des commandes
        List<Order> orders = orderRepository.findByStatus(orderStatus);

        // 3) Jamais null → liste vide si rien
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }

        // 4) Mapping entités -> DTOs
        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long id, UpdateOrderStatusRequestDto request) {

        // 1) Récupération
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée avec id : " + id));

        // 2) BUSINESS RULE : non modifiable
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de modifier une commande " + order.getStatus());
        }

        // 3) Validation nouveau statut
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Statut invalide : " + request.getStatus());
        }

        // garder l'ancien statut pour la métrique
        OrderStatus oldStatus = order.getStatus();

        // 4) Mise à jour
        order.setStatus(newStatus);

        // 5) Sauvegarde
        Order savedOrder = orderRepository.save(order);

        // métrique : changement de statut (old -> new)
        orderMetrics.incrementOrderStatusChanged(oldStatus, newStatus);

        // 6) Mapping
        return orderMapper.toDto(savedOrder);
    }
    @Override
    public void deleteOrder(Long id) {

        // ─────────────────────────────────────────────
        // 1) Vérifier que la commande existe
        // ─────────────────────────────────────────────
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Commande non trouvée avec id : " + id)
                );

        // ─────────────────────────────────────────────
        // 2) BUSINESS RULE : une commande DELIVERED/CANCELLED ne peut plus être modifiée
        //    -> suppression = modification => interdite
        // ─────────────────────────────────────────────
        if (order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Impossible de supprimer une commande " + order.getStatus()
            );
        }

        // ─────────────────────────────────────────────
        // 3) Suppression
        // ─────────────────────────────────────────────
        orderRepository.delete(order);
    }
}