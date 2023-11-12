package ru.skillbox.orderservice.dto;

import lombok.Data;

@Data
public class OrderDto {

    private Long productId;
    private Integer count;
}
