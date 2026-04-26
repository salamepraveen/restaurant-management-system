package com.prav.order.dto;

import com.prav.order.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private String deliveryAddress;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private BigDecimal refundAmount;
    private String refundId;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDTO> items;
}