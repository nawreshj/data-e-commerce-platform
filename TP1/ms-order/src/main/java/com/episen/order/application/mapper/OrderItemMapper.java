package com.episen.order.application.mapper;

import org.springframework.stereotype.Component;

import com.episen.order.application.dto.OrderItemRequestDto;
import com.episen.order.application.dto.OrderItemResponseDto;
import com.episen.order.domain.entity.OrderItem;

/**
 * Mapper pour convertir entre OrderItem et ses DTOs.
 */
@Component
public class OrderItemMapper {

    /**
     * Entité -> DTO de réponse.
     */
    public OrderItemResponseDto toDto(OrderItem item) {
        return OrderItemResponseDto.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    /**
     * DTO de requête -> entité (mapper pauvre, sans logique métier).
     * - on ne met pas l'Order
     * - on ne calcule pas le subtotal
     * - on ne met pas productName/unitPrice (ils viendront du ms-product)
     */
    public OrderItem toEntity(OrderItemRequestDto dto) {
        return OrderItem.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .build();
    }
}