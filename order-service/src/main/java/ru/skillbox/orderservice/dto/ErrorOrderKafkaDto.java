package ru.skillbox.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorOrderKafkaDto {

    private Long orderId;
    private StatusDto statusDto;

    public ErrorOrderKafkaDto() {
    }
}
