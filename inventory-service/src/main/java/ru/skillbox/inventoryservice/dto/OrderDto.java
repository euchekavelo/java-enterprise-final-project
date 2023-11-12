package ru.skillbox.inventoryservice.dto;

import lombok.Data;

@Data
public class OrderDto {

    private Long productId;
    private Integer count;
}
