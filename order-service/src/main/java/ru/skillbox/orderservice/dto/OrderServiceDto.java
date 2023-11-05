package ru.skillbox.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderServiceDto {

    private List<OrderDto> orderDtoList;
    private Integer cost;
    private String destinationAddress;
}
