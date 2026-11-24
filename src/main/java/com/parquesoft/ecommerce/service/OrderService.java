package com.parquesoft.ecommerce.service;

import com.parquesoft.ecommerce.entity.OrderEntity;
import com.parquesoft.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderEntity createOrder(OrderEntity order) {
        if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            throw new IllegalArgumentException("El monto total del pedido debe ser mayor a cero");
        }
        order.setStatus("PENDING");
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<OrderEntity> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public OrderEntity updateOrderStatus(Long id, String newStatus) {
        OrderEntity order = orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));
        if ("SHIPPED".equals(order.getStatus()) && "CANCELLED".equals(newStatus)) {
            throw new IllegalStateException("No se puede cancelar una orden enviada");
        }
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));
        if ("SHIPPED".equals(order.getStatus())) {
            throw new IllegalStateException("No se puede cancelar una orden enviada");
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
}
