package com.episen.order.application.service;

import com.episen.order.application.dto.*;
import java.util.List;

/**
 * Service métier pour la gestion des commandes.
 * Déclare toutes les opérations possibles sur les commandes.
 */
public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto request);

    OrderResponseDto getOrderById(Long id);

    List<OrderResponseDto> getAllOrders();

    List<OrderResponseDto> getOrdersByUser(Long userId);

    List<OrderResponseDto> getOrdersByStatus(String status);

    OrderResponseDto updateOrderStatus(Long id, UpdateOrderStatusRequestDto request);

    void deleteOrder(Long id);
}