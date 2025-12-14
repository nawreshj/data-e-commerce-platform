package com.episen.order.application.service;

import com.episen.order.application.dto.OrderRequestDto;
import com.episen.order.application.dto.OrderResponseDto;
import com.episen.order.application.dto.UpdateOrderStatusRequestDto;
import com.episen.order.application.dto.UserDto;
import com.episen.order.application.mapper.OrderItemMapper;
import com.episen.order.application.mapper.OrderMapper;
import com.episen.order.domain.entity.Order;
import com.episen.order.domain.enums.OrderStatus;
import com.episen.order.domain.repository.OrderRepository;
import com.episen.order.infrastructure.client.ProductClient;
import com.episen.order.infrastructure.client.UserClient;
import com.episen.order.infrastructure.metrics.OrderMetrics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private UserClient userClient;
    @Mock private ProductClient productClient;
    @Mock private OrderMetrics orderMetrics;

    @InjectMocks private OrderServiceImpl orderService;

    // TEST 1 — BUSINESS RULE : DELIVERED/CANCELLED => non modifiable
    @Test
    void updateOrderStatus_shouldThrow_whenOrderIsDeliveredOrCancelled() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        UpdateOrderStatusRequestDto req = new UpdateOrderStatusRequestDto();
        req.setStatus("PENDING");

        // When + Then
        assertThrows(IllegalStateException.class, () -> orderService.updateOrderStatus(1L, req));

        verify(orderRepository, never()).save(any());
        verify(orderMetrics, never()).incrementOrderStatusChanged(any(), any());
    }

    // TEST 2 — Validation : statut invalide => IllegalArgumentException
    @Test
    void updateOrderStatus_shouldThrow_whenNewStatusIsInvalid() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        UpdateOrderStatusRequestDto req = new UpdateOrderStatusRequestDto();
        req.setStatus("BAD_STATUS");

        // When + Then
        assertThrows(IllegalArgumentException.class, () -> orderService.updateOrderStatus(1L, req));

        verify(orderRepository, never()).save(any());
        verify(orderMetrics, never()).incrementOrderStatusChanged(any(), any());
    }

    // TEST 3 — Cas nominal : update OK => save + métrique + retour dto
    @Test
    void updateOrderStatus_shouldSaveAndIncrementMetric_andReturnDto_whenValid() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDto expectedDto = new OrderResponseDto();
        when(orderMapper.toDto(any(Order.class))).thenReturn(expectedDto);

        UpdateOrderStatusRequestDto req = new UpdateOrderStatusRequestDto();
        req.setStatus("DELIVERED");

        // When
        OrderResponseDto result = orderService.updateOrderStatus(1L, req);

        // Then (DTO)
        assertSame(expectedDto, result);

        // Then (save status updated)
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals(OrderStatus.DELIVERED, captor.getValue().getStatus());

        // Then (metric old->new)
        verify(orderMetrics).incrementOrderStatusChanged(OrderStatus.PENDING, OrderStatus.DELIVERED);
    }

    // (Optionnel si tu veux remplacer un des 3) : createOrder should throw if no items
    @Test
    void createOrder_shouldThrow_whenItemsEmpty() {
        OrderRequestDto req = new OrderRequestDto();
        req.setItems(null);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(req));

        verifyNoInteractions(userClient, productClient, orderRepository, orderMetrics);
    }
}