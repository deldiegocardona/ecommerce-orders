package com.parquesoft.ecommerce.service;

import com.parquesoft.ecommerce.entity.OrderEntity;
import com.parquesoft.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrder_Success() {
        OrderEntity order = new OrderEntity();
        order.setCustomerId(1L);
        order.setTotalAmount(185000.50);
        order.setStatus("PAID");
        when(orderRepository.save(order)).thenAnswer(invocation -> {
            order.setId(1L);
            return order;
        });
        OrderEntity saved = orderService.createOrder(order);
        assertSame(order, saved);
        assertEquals(1L, saved.getId());
        assertEquals("PENDING", saved.getStatus());
        assertNotNull(saved.getOrderDate());
        verify(orderRepository).save(order);
    }

    @Test
    void testCreateOrder_ThrowsException_WhenTotalIsZeroOrNegative() {
        for (double amount : new double[]{0.0, -50.0}) {
            OrderEntity order = new OrderEntity();
            order.setCustomerId(1L);
            order.setTotalAmount(amount);
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(order));
            assertEquals("El monto total del pedido debe ser mayor a cero", error.getMessage());
        }
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void testGetOrderById_Success() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertSame(order, orderService.getOrderById(1L).orElseThrow());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(orderService.getOrderById(99L).isEmpty());
    }
}
