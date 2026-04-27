package com.prav.order;

import com.prav.order.dto.RefundResponseDTO;
import com.prav.order.service.RazorpayPaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RazorpayPaymentServiceTest {

    private RazorpayPaymentService razorpayPaymentService;

    @BeforeEach
    void setUp() {
        razorpayPaymentService = new RazorpayPaymentService("rzp_test_id", "rzp_test_secret", "INR", 80);
    }

    @Test
    void testGetKeyId() {
        assertEquals("rzp_test_id", razorpayPaymentService.getKeyId());
    }

    @Test
    void testVerifyPayment_Success() {
        try (MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class)) {
            utilsMockedStatic.when(() -> Utils.verifyPaymentSignature(any(JSONObject.class), anyString()))
                    .thenReturn(true);

            boolean result = razorpayPaymentService.verifyPayment("order_1", "pay_1", "sig_1");
            assertTrue(result);
        }
    }

    @Test
    void testVerifyPayment_Exception() {
        try (MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class)) {
            utilsMockedStatic.when(() -> Utils.verifyPaymentSignature(any(JSONObject.class), anyString()))
                    .thenThrow(new RuntimeException("Signature error"));

            boolean result = razorpayPaymentService.verifyPayment("order_1", "pay_1", "sig_1");
            assertFalse(result);
        }
    }

    @Test
    void testCreateRazorpayOrder_Success() throws Exception {
        Order mockedOrder = mock(Order.class);
        when(mockedOrder.get("id")).thenReturn("order_rzp_123");

        try (MockedConstruction<RazorpayClient> mockedClient = Mockito.mockConstruction(RazorpayClient.class,
                (mock, context) -> {
                    com.razorpay.OrderClient ordersClient = mock(com.razorpay.OrderClient.class);
                    ReflectionTestUtils.setField(mock, "Orders", ordersClient);
                    when(ordersClient.create(any(JSONObject.class))).thenReturn(mockedOrder);
                })) {

            Order result = razorpayPaymentService.createRazorpayOrder(1L, BigDecimal.valueOf(100));

            assertNotNull(result);
            assertEquals("order_rzp_123", result.get("id"));
        }
    }

    @Test
    void testCreateRazorpayOrder_Exception() {
        try (MockedConstruction<RazorpayClient> mockedClient = Mockito.mockConstruction(RazorpayClient.class,
                (mock, context) -> {
                    com.razorpay.OrderClient ordersClient = mock(com.razorpay.OrderClient.class);
                    ReflectionTestUtils.setField(mock, "Orders", ordersClient);
                    when(ordersClient.create(any(JSONObject.class))).thenThrow(new RazorpayException("Error creating order"));
                })) {

            assertThrows(RuntimeException.class, () -> razorpayPaymentService.createRazorpayOrder(1L, BigDecimal.valueOf(100)));
        }
    }

    @Test
    void testProcessRefund_Success() throws Exception {
        Refund mockedRefund = mock(Refund.class);
        when(mockedRefund.get("id")).thenReturn("refund_rzp_123");
        when(mockedRefund.get("status")).thenReturn("processed");

        try (MockedConstruction<RazorpayClient> mockedClient = Mockito.mockConstruction(RazorpayClient.class,
                (mock, context) -> {
                    com.razorpay.PaymentClient paymentsClient = mock(com.razorpay.PaymentClient.class);
                    ReflectionTestUtils.setField(mock, "Payments", paymentsClient);
                    when(paymentsClient.refund(anyString(), any(JSONObject.class))).thenReturn(mockedRefund);
                })) {

            RefundResponseDTO result = razorpayPaymentService.processRefund(1L, BigDecimal.valueOf(100), "pay_1");

            assertNotNull(result);
            assertEquals("refund_rzp_123", result.getRefundId());
            assertEquals("processed", result.getStatus());
            assertEquals(BigDecimal.valueOf(80.0), result.getRefundAmount()); // 80% of 100
        }
    }

    @Test
    void testProcessRefund_Exception() {
        try (MockedConstruction<RazorpayClient> mockedClient = Mockito.mockConstruction(RazorpayClient.class,
                (mock, context) -> {
                    com.razorpay.PaymentClient paymentsClient = mock(com.razorpay.PaymentClient.class);
                    ReflectionTestUtils.setField(mock, "Payments", paymentsClient);
                    when(paymentsClient.refund(anyString(), any(JSONObject.class))).thenThrow(new RazorpayException("Error refunding"));
                })) {

            assertThrows(RuntimeException.class, () -> razorpayPaymentService.processRefund(1L, BigDecimal.valueOf(100), "pay_1"));
        }
    }
}
