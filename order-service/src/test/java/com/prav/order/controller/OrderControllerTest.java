package com.prav.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prav.common.dto.ApiResponse;
import com.prav.common.exception.BadRequestException;
import com.prav.common.exception.ResourceNotFoundException;
import com.prav.order.dto.*;
import com.prav.order.exception.AccessDeniedException;
import com.prav.order.model.Order;
import com.prav.order.service.OrderService;
import com.prav.order.service.ReportService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private OrderController orderController;

    @ControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    // ==================== PLACE ORDER ====================

    @Test
    void placeOrder_success() throws Exception {
        OrderResponseDTO order = OrderResponseDTO.builder()
                .id(1L).userId(10L).restaurantId(100L)
                .totalAmount(BigDecimal.valueOf(50.0))
                .status(Order.OrderStatus.PLACED)
                .build();

        when(orderService.placeOrder(eq(10L), eq(100L), anyString(), anyString(), anyList())).thenReturn(order);

        String body = """
        {
            "restaurantId": 100,
            "deliveryAddress": "123 Main St",
            "paymentMethod": "CARD",
            "items": [{"pizzaId": 1, "size": "MEDIUM", "quantity": 2}]
        }
        """;

        mockMvc.perform(post("/orders")
                .header("X-User-Id", 10)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.restaurantId").value(100));
    }

    @Test
    void placeOrder_validationFails_missingFields() throws Exception {
        String body = """
        {
            "restaurantId": null,
            "items": []
        }
        """;

        mockMvc.perform(post("/orders")
                .header("X-User-Id", 10)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_validationFails_quantityTooHigh() throws Exception {
        String body = """
        {
            "restaurantId": 100,
            "items": [{"pizzaId": 1, "size": "LARGE", "quantity": 25}]
        }
        """;

        mockMvc.perform(post("/orders")
                .header("X-User-Id", 10)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_serviceThrowsException() throws Exception {
        when(orderService.placeOrder(eq(10L), eq(100L), anyString(), anyString(), anyList()))
                .thenThrow(new RuntimeException("Pizza not found"));

        String body = """
        {
            "restaurantId": 100,
            "deliveryAddress": "123 Main St",
            "paymentMethod": "CARD",
            "items": [{"pizzaId": 999, "size": "SMALL", "quantity": 1}]
        }
        """;

        mockMvc.perform(post("/orders")
                .header("X-User-Id", 10)
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET MY ORDERS ====================

    @Test
    void getMyOrders_success() throws Exception {
        OrderResponseDTO order = OrderResponseDTO.builder()
                .id(1L).userId(10L)
                .status(Order.OrderStatus.DELIVERED)
                .build();

        when(orderService.getMyOrders(10L)).thenReturn(List.of(order));

        mockMvc.perform(get("/orders/my")
                .header("X-User-Id", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    // ==================== GET RESTAURANT ORDERS ====================

    @Test
    void getRestaurantOrders_success_asAdmin() throws Exception {
        when(orderService.getRestaurantOrders(100L)).thenReturn(List.of());

        mockMvc.perform(get("/orders/restaurant")
                .header("X-User-Id", 1)
                .header("X-User-Role", "ADMIN")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isOk());
    }

    @Test
    void getRestaurantOrders_success_asStaff() throws Exception {
        when(orderService.getRestaurantOrders(100L)).thenReturn(List.of());

        mockMvc.perform(get("/orders/restaurant")
                .header("X-User-Id", 2)
                .header("X-User-Role", "STAFF")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isOk());
    }

    @Test
    void getRestaurantOrders_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/orders/restaurant")
                .header("X-User-Id", 10)
                .header("X-User-Role", "USER")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isForbidden());
    }

    // ==================== UPDATE STATUS ====================

    @Test
    void updateStatus_success_asAdmin() throws Exception {
        OrderResponseDTO order = OrderResponseDTO.builder()
                .id(1L).status(Order.OrderStatus.CONFIRMED).build();

        when(orderService.updateStatus(1L, "CONFIRMED")).thenReturn(order);

        mockMvc.perform(put("/orders/1/status")
                .header("X-User-Role", "ADMIN")
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    void updateStatus_forbiddenForUser() throws Exception {
        mockMvc.perform(put("/orders/1/status")
                .header("X-User-Role", "USER")
                .param("status", "CONFIRMED"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateStatus_invalidStatus() throws Exception {
        when(orderService.updateStatus(1L, "INVALID_STATUS"))
                .thenThrow(new RuntimeException("Invalid status"));

        mockMvc.perform(put("/orders/1/status")
                .header("X-User-Role", "ADMIN")
                .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_orderNotFound() throws Exception {
        when(orderService.updateStatus(999L, "CONFIRMED"))
                .thenThrow(new ResourceNotFoundException("Order", "id", 999L));

        mockMvc.perform(put("/orders/999/status")
                .header("X-User-Role", "ADMIN")
                .param("status", "CONFIRMED"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET ORDER ITEMS ====================

    @Test
    void getOrderItems_success() throws Exception {
        OrderItemResponseDTO item = OrderItemResponseDTO.builder()
                .pizzaId(1L).pizzaName("Margherita").quantity(2).price(15.0).build();

        when(orderService.getOrderItems(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/orders/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].pizzaName").value("Margherita"));
    }

    // ==================== CANCEL ORDER ====================

    @Test
    void cancelOrder_success() throws Exception {
        OrderResponseDTO order = OrderResponseDTO.builder()
                .id(1L).status(Order.OrderStatus.CANCELLED)
                .cancellationReason("Changed my mind").build();

        when(orderService.cancelOrder(1L, 10L, "Changed my mind")).thenReturn(order);

        String body = """
        {"reason": "Changed my mind"}
        """;

        mockMvc.perform(post("/orders/1/cancel")
                .header("X-User-Id", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void cancelOrder_alreadyDelivered() throws Exception {
        when(orderService.cancelOrder(1L, 10L, "Too late"))
                .thenThrow(new RuntimeException("Cannot cancel a delivered order"));

        String body = """
        {"reason": "Too late"}
        """;

        mockMvc.perform(post("/orders/1/cancel")
                .header("X-User-Id", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    // ==================== PAYMENT ====================

    @Test
    void createPaymentOrder_success() throws Exception {
        PaymentOrderResponseDTO payment = PaymentOrderResponseDTO.builder()
                .razorpayOrderId("order_123").amount(BigDecimal.valueOf(50.0))
                .currency("INR").key("rzp_test_xxx").orderId(1L).build();

        when(orderService.createPaymentOrder(1L)).thenReturn(payment);

        mockMvc.perform(post("/orders/1/payment/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.razorpayOrderId").value("order_123"));
    }

    @Test
    void verifyPayment_success() throws Exception {
        OrderResponseDTO order = OrderResponseDTO.builder()
                .id(1L).status(Order.OrderStatus.CONFIRMED)
                .paymentStatus(Order.PaymentStatus.COMPLETED).build();

        PaymentVerifyRequestDTO request = new PaymentVerifyRequestDTO();
        request.setRazorpayOrderId("order_123");
        request.setRazorpayPaymentId("pay_123");
        request.setRazorpaySignature("abc123");

        when(orderService.verifyPayment(eq(1L), any(PaymentVerifyRequestDTO.class))).thenReturn(order);

        mockMvc.perform(post("/orders/1/payment/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.paymentStatus").value("COMPLETED"));
    }

    // ==================== REPORTS ====================

    @Test
    void getRevenue_success_asAdmin() throws Exception {
        Map<String, Object> report = new HashMap<>();
        report.put("totalRevenue", 1000.0);
        report.put("totalOrders", 50);

        when(reportService.getRevenueByRestaurant(100L)).thenReturn(report);

        mockMvc.perform(get("/orders/reports/revenue")
                .header("X-User-Role", "ADMIN")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalRevenue").value(1000.0));
    }

    @Test
    void getRevenue_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/orders/reports/revenue")
                .header("X-User-Role", "USER")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRevenueByRange_success() throws Exception {
        Map<String, Object> report = new HashMap<>();
        report.put("totalRevenue", 500.0);

        when(reportService.getRevenueByDateRange(eq(100L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(report);

        mockMvc.perform(get("/orders/reports/revenue/range")
                .header("X-User-Role", "ADMIN")
                .header("X-Restaurant-Id", 100)
                .param("start", "2025-01-01T00:00:00")
                .param("end", "2025-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalRevenue").value(500.0));
    }

    @Test
    void getDailyRevenue_success() throws Exception {
        when(reportService.getDailyRevenue(eq(100L), eq(7))).thenReturn(List.of());

        mockMvc.perform(get("/orders/reports/revenue/daily")
                .header("X-User-Role", "ADMIN")
                .header("X-Restaurant-Id", 100)
                .param("days", "7"))
                .andExpect(status().isOk());
    }

    @Test
    void getDailyRevenue_successForStaff() throws Exception {
        mockMvc.perform(get("/orders/reports/revenue/daily")
                .header("X-User-Role", "STAFF")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isOk());
    }

    @Test
    void getPopularPizzas_success() throws Exception {
        when(reportService.getPopularPizzas(100L)).thenReturn(List.of());

        mockMvc.perform(get("/orders/reports/pizzas/popular")
                .header("X-User-Role", "ADMIN")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isOk());
    }

    @Test
    void getPopularPizzas_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/orders/reports/pizzas/popular")
                .header("X-User-Role", "USER")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTopCustomers_success() throws Exception {
        when(reportService.getTopCustomers(100L)).thenReturn(List.of());

        mockMvc.perform(get("/orders/reports/customers/top")
                .header("X-User-Role", "ADMIN")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderStatusSummary_success_asAdmin() throws Exception {
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", 10);

        when(reportService.getOrderStatusSummary(100L)).thenReturn(summary);

        mockMvc.perform(get("/orders/reports/orders/status")
                .header("X-User-Role", "ADMIN")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(10));
    }

    @Test
    void getOrderStatusSummary_success_asStaff() throws Exception {
        when(reportService.getOrderStatusSummary(100L)).thenReturn(new HashMap<>());

        mockMvc.perform(get("/orders/reports/orders/status")
                .header("X-User-Role", "STAFF")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderStatusSummary_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/orders/reports/orders/status")
                .header("X-User-Role", "USER")
                .header("X-Restaurant-Id", 100))
                .andExpect(status().isForbidden());
    }
}