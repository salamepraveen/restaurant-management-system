package com.prav.order;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import com.prav.order.dto.*;

public class DTOCoverageTest {

    @Test
    void testOrderCancelRequestDTO() {
        OrderCancelRequestDTO dto = new OrderCancelRequestDTO();
        dto.setReason("Changed mind");
        assertEquals("Changed mind", dto.getReason());
    }

    @Test
    void testOrderItemDTO() {
        OrderRequestDTO.OrderItemDTO dto = new OrderRequestDTO.OrderItemDTO();
        dto.setPizzaId(1L);
        dto.setSize("LARGE");
        dto.setQuantity(2);
        dto.setToppings(List.of("Cheese"));
        
        assertEquals(1L, dto.getPizzaId());
        assertEquals("LARGE", dto.getSize());
        assertEquals(2, dto.getQuantity());
        assertEquals(1, dto.getToppings().size());
    }

    @Test
    void testOrderItemResponseDTO() {
        OrderItemResponseDTO dto = OrderItemResponseDTO.builder()
                .pizzaId(1L)
                .pizzaName("Margherita")
                .size("LARGE")
                .quantity(2)
                .price(15.99)
                .toppings("Extra Cheese")
                .build();
        
        assertEquals(1L, dto.getPizzaId());
        assertEquals("Margherita", dto.getPizzaName());
        assertEquals("LARGE", dto.getSize());
        assertEquals(2, dto.getQuantity());
        assertEquals(15.99, dto.getPrice());
        assertEquals("Extra Cheese", dto.getToppings());
        
        dto.setPizzaId(2L);
        assertEquals(2L, dto.getPizzaId());
    }

    @Test
    void testOrderRequestDTO() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(1L);
        dto.setDeliveryAddress("Home");
        dto.setPaymentMethod("CARD");
        dto.setItems(List.of());
        
        assertEquals(1L, dto.getRestaurantId());
        assertEquals("Home", dto.getDeliveryAddress());
        assertEquals("CARD", dto.getPaymentMethod());
        assertNotNull(dto.getItems());
    }

    @Test
    void testOrderResponseDTO() {
        LocalDateTime now = LocalDateTime.now();
        OrderResponseDTO dto = OrderResponseDTO.builder()
                .id(1L)
                .userId(2L)
                .restaurantId(3L)
                .deliveryAddress("Home")
                .paymentMethod("CARD")
                .totalAmount(BigDecimal.TEN)
                .status(com.prav.order.model.Order.OrderStatus.PLACED)
                .paymentStatus(com.prav.order.model.Order.PaymentStatus.COMPLETED)
                .razorpayOrderId("rzp_1")
                .razorpayPaymentId("pay_1")
                .refundAmount(BigDecimal.ONE)
                .refundId("ref_1")
                .cancellationReason("N/A")
                .createdAt(now)
                .items(List.of())
                .build();
                
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals(3L, dto.getRestaurantId());
        assertEquals("Home", dto.getDeliveryAddress());
        assertEquals("CARD", dto.getPaymentMethod());
        assertEquals(BigDecimal.TEN, dto.getTotalAmount());
        assertEquals(com.prav.order.model.Order.OrderStatus.PLACED, dto.getStatus());
        assertEquals(com.prav.order.model.Order.PaymentStatus.COMPLETED, dto.getPaymentStatus());
        assertEquals("rzp_1", dto.getRazorpayOrderId());
        assertEquals("pay_1", dto.getRazorpayPaymentId());
        assertEquals(BigDecimal.ONE, dto.getRefundAmount());
        assertEquals("ref_1", dto.getRefundId());
        assertEquals("N/A", dto.getCancellationReason());
        assertEquals(now, dto.getCreatedAt());
        assertNotNull(dto.getItems());
        
        dto.setId(99L);
        assertEquals(99L, dto.getId());
    }

    @Test
    void testOrderStatusDTO() {
        OrderStatusDTO dto = new OrderStatusDTO();
        dto.setStatus("CONFIRMED");
        assertEquals("CONFIRMED", dto.getStatus());
    }

    @Test
    void testPaymentOrderResponseDTO() {
        PaymentOrderResponseDTO dto = PaymentOrderResponseDTO.builder()
                .razorpayOrderId("rzp_123")
                .amount(BigDecimal.TEN)
                .currency("INR")
                .key("key_123")
                .orderStatus("created")
                .orderId(1L)
                .build();
                
        assertEquals("rzp_123", dto.getRazorpayOrderId());
        assertEquals(BigDecimal.TEN, dto.getAmount());
        assertEquals("INR", dto.getCurrency());
        assertEquals("key_123", dto.getKey());
        assertEquals("created", dto.getOrderStatus());
        assertEquals(1L, dto.getOrderId());
        
        dto.setCurrency("USD");
        assertEquals("USD", dto.getCurrency());
    }

    @Test
    void testPaymentVerifyRequestDTO() {
        PaymentVerifyRequestDTO dto = new PaymentVerifyRequestDTO();
        dto.setRazorpayOrderId("rzp_1");
        dto.setRazorpayPaymentId("pay_1");
        dto.setRazorpaySignature("sig_1");
        
        assertEquals("rzp_1", dto.getRazorpayOrderId());
        assertEquals("pay_1", dto.getRazorpayPaymentId());
        assertEquals("sig_1", dto.getRazorpaySignature());
    }

    @Test
    void testPizzaDTO() {
        PizzaDTO dto = new PizzaDTO();
        dto.setId(1L);
        dto.setName("Pizza");
        dto.setPrice(10.0);
        dto.setBasePrice(8.0);
        
        assertEquals(1L, dto.getId());
        assertEquals("Pizza", dto.getName());
        assertEquals(10.0, dto.getPrice());
        assertEquals(8.0, dto.getBasePrice());
    }

    @Test
    void testRefundResponseDTO() {
        RefundResponseDTO dto = RefundResponseDTO.builder()
                .refundId("ref_1")
                .status("processed")
                .refundAmount(BigDecimal.TEN)
                .build();
                
        assertEquals("ref_1", dto.getRefundId());
        assertEquals("processed", dto.getStatus());
        assertEquals(BigDecimal.TEN, dto.getRefundAmount());
        
        dto.setStatus("failed");
        assertEquals("failed", dto.getStatus());
    }

    @Test
    void testUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("user");
        dto.setRole("USER");
        dto.setBasePrice(10.0);
        
        assertEquals(1L, dto.getId());
        assertEquals("user", dto.getUsername());
        assertEquals("USER", dto.getRole());
        assertEquals(10.0, dto.getBasePrice());
    }
}
