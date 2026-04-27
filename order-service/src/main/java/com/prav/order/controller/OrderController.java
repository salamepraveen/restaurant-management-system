package com.prav.order.controller;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prav.order.dto.*;
import com.prav.order.exception.AccessDeniedException;
import com.prav.order.service.OrderService;
import com.prav.order.service.ReportService;
import com.prav.common.dto.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService service;
    private final ReportService reportService;

    public OrderController(OrderService service, ReportService reportService) {
        this.service = service;
        this.reportService = reportService;
    }

    //  ORDER CRUD 

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody OrderRequestDTO request) {

        List<Map<String, Object>> items = request.getItems().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("pizzaId", item.getPizzaId());
                    map.put("quantity", item.getQuantity());
                    map.put("size", item.getSize());
                    map.put("toppings", item.getToppings());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());

        OrderResponseDTO order = service.placeOrder(userId, request.getRestaurantId(), request.getDeliveryAddress(), request.getPaymentMethod(), items);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<OrderResponseDTO>builder()
                        .success(true)
                        .message("Order placed successfully")
                        .data(order)
                        .build());
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getMyOrders(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(
                ApiResponse.<List<OrderResponseDTO>>builder()
                        .success(true)
                        .message("Orders retrieved successfully")
                        .data(service.getMyOrders(userId))
                        .build());
    }

    @GetMapping("/restaurant")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getRestaurantOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Restaurant-Id") Long restaurantId) {

        if (!"STAFF".equals(role) && !"ADMIN".equals(role))
            throw new AccessDeniedException("Only STAFF or ADMIN can view restaurant orders");

        return ResponseEntity.ok(
                ApiResponse.<List<OrderResponseDTO>>builder()
                        .success(true)
                        .message("Restaurant orders retrieved successfully")
                        .data(service.getRestaurantOrders(restaurantId))
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<OrderResponseDTO>builder()
                        .success(true)
                        .message("Order retrieved successfully")
                        .data(service.getOrderById(id))
                        .build());
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<ApiResponse<List<OrderItemResponseDTO>>> getOrderItems(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<List<OrderItemResponseDTO>>builder()
                        .success(true)
                        .message("Order items retrieved successfully")
                        .data(service.getOrderItems(id))
                        .build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestParam String status) {

        if (!"STAFF".equals(role) && !"ADMIN".equals(role))
            throw new AccessDeniedException("Only STAFF or ADMIN can update order status");

        return ResponseEntity.ok(
                ApiResponse.<OrderResponseDTO>builder()
                        .success(true)
                        .message("Order status updated to " + status)
                        .data(service.updateStatus(id, status))
                        .build());
    }

    //  PAYMENT 

    @PostMapping("/{id}/payment/create")
    public ResponseEntity<ApiResponse<PaymentOrderResponseDTO>> createPaymentOrder(@PathVariable Long id) {
        PaymentOrderResponseDTO payment = service.createPaymentOrder(id);
        return ResponseEntity.ok(
                ApiResponse.<PaymentOrderResponseDTO>builder()
                        .success(true)
                        .message("Payment order created")
                        .data(payment)
                        .build());
    }

    @PostMapping("/{id}/payment/verify")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> verifyPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentVerifyRequestDTO request) {
        OrderResponseDTO order = service.verifyPayment(id, request);
        return ResponseEntity.ok(
                ApiResponse.<OrderResponseDTO>builder()
                        .success(true)
                        .message("Payment verified and order confirmed")
                        .data(order)
                        .build());
    }

    //  CANCEL 

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody OrderCancelRequestDTO request) {
        OrderResponseDTO order = service.cancelOrder(id, userId, request.getReason());
        return ResponseEntity.ok(
                ApiResponse.<OrderResponseDTO>builder()
                        .success(true)
                        .message("Order cancelled")
                        .data(order)
                        .build());
    }

    //  REPORTS 

    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenue(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Restaurant-Id") Long restaurantId) {
        if (!"ADMIN".equals(role)) throw new AccessDeniedException("Only ADMIN can view revenue");
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true).message("Revenue retrieved")
                        .data(reportService.getRevenueByRestaurant(restaurantId)).build());
    }

    @GetMapping("/reports/revenue/range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueByRange(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Restaurant-Id") Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (!"ADMIN".equals(role)) throw new AccessDeniedException("Only ADMIN can view revenue");
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true).message("Revenue range retrieved")
                        .data(reportService.getRevenueByDateRange(restaurantId, start, end)).build());
    }

    @GetMapping("/reports/revenue/daily")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDailyRevenue(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Restaurant-Id") Long restaurantId,
            @RequestParam(defaultValue = "7") int days) {
        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) throw new AccessDeniedException("Only STAFF or ADMIN");
        return ResponseEntity.ok(
                ApiResponse.<List<Map<String, Object>>>builder()
                        .success(true).message("Daily revenue retrieved")
                        .data(reportService.getDailyRevenue(restaurantId, days)).build());
    }

    @GetMapping("/reports/pizzas/popular")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPopularPizzas(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Restaurant-Id") Long restaurantId) {
        if (!"ADMIN".equals(role)) throw new AccessDeniedException("Only ADMIN");
        return ResponseEntity.ok(
                ApiResponse.<List<Map<String, Object>>>builder()
                        .success(true).message("Popular pizzas retrieved")
                        .data(reportService.getPopularPizzas(restaurantId)).build());
    }

    @GetMapping("/reports/customers/top")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopCustomers(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Restaurant-Id") Long restaurantId) {
        if (!"ADMIN".equals(role)) throw new AccessDeniedException("Only ADMIN");
        return ResponseEntity.ok(
                ApiResponse.<List<Map<String, Object>>>builder()
                        .success(true).message("Top customers retrieved")
                        .data(reportService.getTopCustomers(restaurantId)).build());
    }

    @GetMapping("/reports/orders/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderStatusSummary(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Restaurant-Id") Long restaurantId) {
        if (!"ADMIN".equals(role) && !"STAFF".equals(role))
            throw new AccessDeniedException("Only STAFF or ADMIN");
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true).message("Status summary retrieved")
                        .data(reportService.getOrderStatusSummary(restaurantId)).build());
    }
}