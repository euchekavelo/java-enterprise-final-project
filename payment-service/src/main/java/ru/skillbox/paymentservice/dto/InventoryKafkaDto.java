package ru.skillbox.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InventoryKafkaDto {

    private Long transactionId;
    private Long userId;
    private List<OrderDto> orderDtoList;
    private Long orderId;
    private String destinationAddress;
    private String authHeaderValue;

    public InventoryKafkaDto() {
    }
}
