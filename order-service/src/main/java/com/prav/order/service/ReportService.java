package com.prav.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prav.order.model.Order;
import com.prav.order.model.OrderItem;
import com.prav.order.repository.OrderItemRepository;
import com.prav.order.repository.OrderRepository;

@Service
public class ReportService {

    @Autowired
    private OrderRepository repo;

    @Autowired
    private OrderItemRepository itemRepo;

    // ==================== Revenue by Restaurant ====================

    public Map<String, Object> getRevenueByRestaurant(Long restaurantId) {
        List<Order> orders = repo.findByRestaurantId(restaurantId);

        double totalRevenue = 0;
        int totalOrders = 0;
        int pendingOrders = 0;
        int completedOrders = 0;
        int cancelledOrders = 0;

        for (Order order : orders) {
            totalRevenue += order.getTotalAmount().doubleValue();
            totalOrders++;
            switch (order.getStatus()) {
                case PLACED, CONFIRMED, PREPARING, OUT_FOR_DELIVERY -> pendingOrders++;
                case DELIVERED -> completedOrders++;
                case CANCELLED -> cancelledOrders++;
            }
        }

        Map<String, Object> report = new HashMap<>();
        report.put("restaurantId", restaurantId);
        report.put("totalRevenue", round(totalRevenue));
        report.put("totalOrders", totalOrders);
        report.put("completedOrders", completedOrders);
        report.put("pendingOrders", pendingOrders);
        report.put("cancelledOrders", cancelledOrders);
        report.put("averageOrderValue", totalOrders > 0 ? round(totalRevenue / totalOrders) : 0);
        return report;
    }

    // ==================== Revenue by Date Range ====================

    public Map<String, Object> getRevenueByDateRange(Long restaurantId,
            LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> allOrders = repo.findByRestaurantId(restaurantId);

        double totalRevenue = 0;
        int totalOrders = 0;
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : allOrders) {
            if (order.getCreatedAt() != null
                    && !order.getCreatedAt().isBefore(startDate)
                    && !order.getCreatedAt().isAfter(endDate)
                    && order.getStatus() != Order.OrderStatus.CANCELLED) {
                totalRevenue += order.getTotalAmount().doubleValue();
                totalOrders++;
                filteredOrders.add(order);
            }
        }

        Map<String, Object> report = new HashMap<>();
        report.put("restaurantId", restaurantId);
        report.put("startDate", startDate.toString());
        report.put("endDate", endDate.toString());
        report.put("totalRevenue", round(totalRevenue));
        report.put("totalOrders", totalOrders);
        report.put("orders", filteredOrders);
        return report;
    }

    // ==================== Daily Revenue ====================

    public List<Map<String, Object>> getDailyRevenue(Long restaurantId, int days) {
        List<Order> orders = repo.findByRestaurantId(restaurantId);
        Map<String, Double> dailyMap = new HashMap<>();
        Map<String, Integer> orderCountMap = new HashMap<>();

        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);

        for (Order order : orders) {
            if (order.getCreatedAt() != null
                    && !order.getCreatedAt().isBefore(cutoff)
                    && order.getStatus() != Order.OrderStatus.CANCELLED) {
                String date = order.getCreatedAt().toLocalDate().toString();
                dailyMap.put(date, dailyMap.getOrDefault(date, 0.0) + order.getTotalAmount().doubleValue());
                orderCountMap.put(date, orderCountMap.getOrDefault(date, 0) + 1);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : dailyMap.entrySet()) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", entry.getKey());
            day.put("revenue", round(entry.getValue()));
            day.put("orders", orderCountMap.get(entry.getKey()));
            result.add(day);
        }

        result.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));
        return result;
    }

    // ==================== Popular Pizzas ====================

    public List<Map<String, Object>> getPopularPizzas(Long restaurantId) {
        List<Order> orders = repo.findByRestaurantId(restaurantId);

        Map<String, Integer> pizzaCount = new HashMap<>();
        Map<String, Double> pizzaRevenue = new HashMap<>();

        for (Order order : orders) {
            if (order.getStatus() != Order.OrderStatus.CANCELLED) {
                List<OrderItem> items = itemRepo.findByOrderId(order.getId());
                for (OrderItem item : items) {
                    String name = item.getPizzaName();
                    if (name != null) {
                        pizzaCount.put(name, pizzaCount.getOrDefault(name, 0) + item.getQuantity());
                        pizzaRevenue.put(name,
                                pizzaRevenue.getOrDefault(name, 0.0)
                                        + (item.getPrice().doubleValue() * item.getQuantity()));
                    }
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : pizzaCount.entrySet()) {
            Map<String, Object> pizza = new HashMap<>();
            pizza.put("pizzaName", entry.getKey());
            pizza.put("totalQuantitySold", entry.getValue());
            pizza.put("totalRevenue", round(pizzaRevenue.get(entry.getKey())));
            result.add(pizza);
        }

        result.sort((a, b) -> (Integer) b.get("totalQuantitySold") - (Integer) a.get("totalQuantitySold"));
        return result;
    }

    // ==================== Top Customers ====================

    public List<Map<String, Object>> getTopCustomers(Long restaurantId) {
        List<Order> orders = repo.findByRestaurantId(restaurantId);

        Map<Long, Double> customerSpending = new HashMap<>();
        Map<Long, Integer> customerOrderCount = new HashMap<>();

        for (Order order : orders) {
            if (order.getUserId() != null && order.getStatus() != Order.OrderStatus.CANCELLED) {
                customerSpending.put(order.getUserId(),
                        customerSpending.getOrDefault(order.getUserId(), 0.0)
                                + order.getTotalAmount().doubleValue());
                customerOrderCount.put(order.getUserId(),
                        customerOrderCount.getOrDefault(order.getUserId(), 0) + 1);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : customerSpending.entrySet()) {
            Map<String, Object> customer = new HashMap<>();
            customer.put("userId", entry.getKey());
            customer.put("totalSpent", round(entry.getValue()));
            customer.put("totalOrders", customerOrderCount.get(entry.getKey()));
            result.add(customer);
        }

        result.sort((a, b) -> Double.compare((Double) b.get("totalSpent"), (Double) a.get("totalSpent")));
        return result;
    }

    // ==================== Order Status Summary ====================

    public Map<String, Object> getOrderStatusSummary(Long restaurantId) {
        List<Order> orders = repo.findByRestaurantId(restaurantId);

        int placed = 0, confirmed = 0, preparing = 0, outForDelivery = 0;
        int delivered = 0, cancelled = 0;

        for (Order order : orders) {
            switch (order.getStatus()) {
                case PLACED -> placed++;
                case CONFIRMED -> confirmed++;
                case PREPARING -> preparing++;
                case OUT_FOR_DELIVERY -> outForDelivery++;
                case DELIVERED -> delivered++;
                case CANCELLED -> cancelled++;
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("placed", placed);
        summary.put("confirmed", confirmed);
        summary.put("preparing", preparing);
        summary.put("outForDelivery", outForDelivery);
        summary.put("delivered", delivered);
        summary.put("cancelled", cancelled);
        summary.put("total", orders.size());
        return summary;
    }

    // ==================== Helper ====================

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}