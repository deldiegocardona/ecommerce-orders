package com.parquesoft.ecommerce.controller;

import com.parquesoft.ecommerce.entity.OrderEntity;
import com.parquesoft.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Gestion de pedidos de clientes")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Crear un pedido", description = "Asigna PENDING y fecha actual cuando no se proporciona")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado"),
        @ApiResponse(responseCode = "400", description = "Monto invalido")
    })
    public ResponseEntity<OrderEntity> createOrder(@RequestBody OrderEntity order) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(order));
    } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    public List<OrderEntity> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un pedido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<OrderEntity> getOrderById(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        return orderService.getOrderById(id)
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Consultar los pedidos de un cliente")
    @ApiResponse(responseCode = "200", description = "Historial del cliente")
    public List<OrderEntity> getOrdersByCustomerId(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar pedidos por estado")
    @ApiResponse(responseCode = "200", description = "Pedidos filtrados")
    public List<OrderEntity> getOrdersByStatus(
            @Parameter(description = "Estado del pedido", example = "PENDING") @PathVariable String status) {
        return orderService.getOrdersByStatus(status);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar el estado de un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "409", description = "No se puede cancelar un pedido enviado")
    })
    public ResponseEntity<OrderEntity> updateOrderStatus(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado", example = "PAID") @RequestParam String status) {
        try {
            return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
