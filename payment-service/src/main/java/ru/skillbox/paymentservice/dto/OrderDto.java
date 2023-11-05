package ru.skillbox.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDto {

    private Long productId;
    private Integer count;

    public OrderDto() {
    }
}
