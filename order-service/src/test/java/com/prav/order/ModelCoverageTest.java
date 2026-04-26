package com.prav.order;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import com.prav.order.model.Order;
import com.prav.order.model.OrderItem;

public class ModelCoverageTest {

    @Test
    void testOrder() {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();
        
        order.setId(1L);
        order.setUserId(2L);
        order.setRestaurantId(3L);
        order.setDeliveryAddress("Address");
        order.setPaymentMethod("CARD");
        order.setTotalAmount(BigDecimal.TEN);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setPaymentStatus(Order.PaymentStatus.COMPLETED);
        order.setRazorpayOrderId("rzp_order");
        order.setRazorpayPaymentId("rzp_pay");
        order.setRazorpaySignature("rzp_sig");
        order.setRefundAmount(BigDecimal.ONE);
        order.setRefundId("ref_1");
        order.setCancellationReason("Reason");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        
        OrderItem item = new OrderItem();
        item.setId(1L);
        order.setOrderItems(List.of(item));

        assertEquals(1L, order.getId());
        assertEquals(2L, order.getUserId());
        assertEquals(3L, order.getRestaurantId());
        assertEquals("Address", order.getDeliveryAddress());
        assertEquals("CARD", order.getPaymentMethod());
        assertEquals(BigDecimal.TEN, order.getTotalAmount());
        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(Order.PaymentStatus.COMPLETED, order.getPaymentStatus());
        assertEquals("rzp_order", order.getRazorpayOrderId());
        assertEquals("rzp_pay", order.getRazorpayPaymentId());
        assertEquals("rzp_sig", order.getRazorpaySignature());
        assertEquals(BigDecimal.ONE, order.getRefundAmount());
        assertEquals("ref_1", order.getRefundId());
        assertEquals("Reason", order.getCancellationReason());
        assertEquals(now, order.getCreatedAt());
        assertEquals(now, order.getUpdatedAt());
        assertEquals(1, order.getOrderItems().size());
        
        assertNotNull(order.toString());
        assertNotEquals(0, order.hashCode());
        
        Order order2 = new Order(1L, 2L, 3L, "Address", "CARD", BigDecimal.TEN, Order.OrderStatus.CONFIRMED, Order.PaymentStatus.COMPLETED, "rzp_order", "rzp_pay", "rzp_sig", BigDecimal.ONE, "ref_1", "Reason", now, now, List.of(item));
        assertEquals(order, order2);
    }

    @Test
    void testOrderItem() {
        OrderItem item = new OrderItem();
        Order order = new Order();
        order.setId(1L);
        
        item.setId(1L);
        item.setOrder(order);
        item.setPizzaId(2L);
        item.setPizzaName("Margherita");
        item.setSize("LARGE");
        item.setQuantity(2);
        item.setPrice(15.99);
        item.setToppings("Extra Cheese");

        assertEquals(1L, item.getId());
        assertEquals(order, item.getOrder());
        assertEquals(2L, item.getPizzaId());
        assertEquals("Margherita", item.getPizzaName());
        assertEquals("LARGE", item.getSize());
        assertEquals(2, item.getQuantity());
        assertEquals(15.99, item.getPrice());
        assertEquals("Extra Cheese", item.getToppings());
        
        assertNotNull(item.toString());
        assertNotEquals(0, item.hashCode());
        
        OrderItem item2 = new OrderItem(1L, order, 2L, "Margherita", "LARGE", 2, 15.99, "Extra Cheese");
        assertEquals(item, item2);
    }
}
