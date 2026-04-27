package com.prav.order.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.prav.order.dto.RefundResponseDTO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.razorpay.Utils;
import java.math.BigDecimal;

@Service
public class RazorpayPaymentService {
 
    private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentService.class);
//    private static final String BORDER = "========================================";

    private final String keyId;
    private final String keySecret;
    private final String currency;
    private final int refundPercentage;

    public RazorpayPaymentService(
            @Value("${razorpay.key-id}") String keyId,
            @Value("${razorpay.key-secret}") String keySecret,
            @Value("${razorpay.currency}") String currency,
            @Value("${razorpay.refund-percentage}") int refundPercentage) {
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.currency = currency;
        this.refundPercentage = refundPercentage;
    }

    private RazorpayClient getClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }

    public Order createRazorpayOrder(Long orderId, BigDecimal amount) {
        try {
            RazorpayClient client = getClient();
            JSONObject orderRequest = new JSONObject();
            long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValue();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_rcpt_" + orderId);

            Order razorpayOrder = client.Orders.create(orderRequest);

//            log.info("========================================");
            log.info("  RAZORPAY ORDER CREATED");
            log.info("  Order ID: {}", orderId);
            log.info("  Razorpay Order ID: {}", razorpayOrder.get("id").toString());
            log.info("  Amount: {} {}", currency, amount);
//            log.info("========================================");

            return razorpayOrder;
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create payment order: " + e.getMessage(), e);
        }
    }

    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
            log.info("  [PAYMENT] Verifying...");
            log.info("  [PAYMENT] Order: {} | Payment: {}", orderId, paymentId);

            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(attributes, keySecret);
        } catch (Exception e) {
            log.error("  [PAYMENT] !! Verification failed", e);
            return false;
        }
    }

    public RefundResponseDTO processRefund(Long orderId, BigDecimal originalAmount, String razorpayPaymentId) {
        try {
            RazorpayClient client = getClient();

            BigDecimal refundFactor = BigDecimal.valueOf(refundPercentage).divide(BigDecimal.valueOf(100));
            BigDecimal refundAmount = originalAmount.multiply(refundFactor);
            BigDecimal deduction = originalAmount.subtract(refundAmount);
            long refundInPaise = refundAmount.multiply(BigDecimal.valueOf(100)).longValue();

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", refundInPaise);
            refundRequest.put("speed", "normal");

//            log.info("========================================");
            log.info("  PROCESSING REFUND");
            log.info("  Order ID: {}", orderId);
            log.info("  Original: {} {}", currency, originalAmount);
            log.info("  Refund ({}%): {} {}", refundPercentage, currency, refundAmount);
            log.info("  Deduction: {} {}", currency, deduction);
//            log.info("========================================");

            Refund refund = client.Payments.refund(razorpayPaymentId, refundRequest);

            log.info("  [REFUND] ID: {} | Status: {}", refund.get("id"), refund.get("status"));

            return RefundResponseDTO.builder()
                    .orderId(orderId)
                    .originalAmount(originalAmount)
                    .refundAmount(refundAmount)
                    .deductionAmount(deduction)
                    .refundPercentage(refundPercentage)
                    .refundId(refund.get("id").toString())
                    .status(refund.get("status").toString())
                    .message(refundPercentage + "% refund processed. " + currency + " " + deduction + " deducted as cancellation fee.")
                    .build();
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to process refund: " + e.getMessage(), e);
        }
    }

    public String getKeyId() {
        return keyId;
    }
}
