package com.prav.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class OrderRequestDTO {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "At least one item is required")
    @Valid
    private List<OrderItemDTO> items;

    @NotNull(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    

    public Long getRestaurantId() {
		return restaurantId;
	}



	public void setRestaurantId(Long restaurantId) {
		this.restaurantId = restaurantId;
	}



	public List<OrderItemDTO> getItems() {
		return items;
	}



	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }



	// Nested DTO for each item
    public static class OrderItemDTO {

        @NotNull(message = "Pizza ID is required")
        private Long pizzaId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 20, message = "Maximum 20 pizzas per item")
        private Integer quantity;

        private String size;

        private List<String> toppings;

		public Long getPizzaId() {
			return pizzaId;
		}

		public void setPizzaId(Long pizzaId) {
			this.pizzaId = pizzaId;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public List<String> getToppings() {
            return toppings;
        }

        public void setToppings(List<String> toppings) {
            this.toppings = toppings;
        }

       
    }
}