package com.prav.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {
    private Long pizzaId;
    private String pizzaName;
    private String size;
    private Integer quantity;
    private Double price;
}