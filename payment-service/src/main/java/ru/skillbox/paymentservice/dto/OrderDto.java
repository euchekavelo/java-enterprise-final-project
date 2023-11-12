package ru.skillbox.paymentservice.dto;

import lombok.Data;

@Data
public class OrderDto {

    private Long productId;
    private Integer count;
}
