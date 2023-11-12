package ru.skillbox.paymentservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class InventoryKafkaDto {

    private Long userId;
    private List<OrderDto> orderDtoList;
    private Long orderId;
    private String destinationAddress;
    private String authHeaderValue;
}
