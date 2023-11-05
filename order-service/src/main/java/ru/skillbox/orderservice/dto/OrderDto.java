package ru.skillbox.orderservice.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class OrderDto {

    private Long productId;
    private Integer count;

    public OrderDto() {
    }
}
