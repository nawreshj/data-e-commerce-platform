package com.episen.order.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.episen.order.application.dto.OrderItemResponseDto;
import com.episen.order.application.dto.OrderRequestDto;
import com.episen.order.application.dto.OrderResponseDto;
import com.episen.order.domain.entity.Order;

/**
 * Mapper pour convertir entre Order et ses DTOs.
 */
@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * Convertit une entité Order en OrderResponseDto.
     */
    public OrderResponseDto toDto(Order order) {

        List<OrderItemResponseDto> items = order.getItems() == null
                ? List.of()
                : order.getItems().stream()
                        .map(orderItemMapper::toDto)
                        .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(items)
                .build();
    }


    /**
     * Mapper "pauvre" : Request DTO -> Order sans logique métier.
     * Pas de status, pas de totalAmount, pas d'items ici.
     */
    public Order toEntityFromRequest(OrderRequestDto dto) {
        return Order.builder()
                .userId(dto.getUserId())
                .shippingAddress(dto.getShippingAddress())
                // orderDate, status, totalAmount, items
                // seront gérés dans le service
                .build();
    }
}