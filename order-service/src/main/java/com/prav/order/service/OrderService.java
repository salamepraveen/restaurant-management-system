package com.prav.order.service;

import com.prav.order.dto.*;
import java.util.List;
import java.util.Map;

public interface OrderService {

    // Order
    OrderResponseDTO placeOrder(Long userId, Long restaurantId, List<Map<String, Object>> items);
    List<OrderResponseDTO> getMyOrders(Long userId);
    List<OrderResponseDTO> getRestaurantOrders(Long restaurantId);
    OrderResponseDTO getOrderById(Long id);
    List<OrderItemResponseDTO> getOrderItems(Long id);
    OrderResponseDTO updateStatus(Long id, String status);

    // Payment
    PaymentOrderResponseDTO createPaymentOrder(Long orderId);
    OrderResponseDTO verifyPayment(Long orderId, PaymentVerifyRequestDTO request);

    // Cancel
    OrderResponseDTO cancelOrder(Long orderId, Long userId, String reason);
}